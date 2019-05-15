package com.elorating.web.controller;

import com.elorating.league.LeagueDocument;
import com.elorating.match.MatchDocument;
import com.elorating.player.PlayerService;
import com.elorating.match.MatchService;
import com.elorating.web.utils.SortUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api(value = "Matches", description = "Matches API")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

    @CrossOrigin
    @RequestMapping(value = "/matches/{matchId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get match", notes = "Return match by match id")
    public ResponseEntity<MatchDocument> getMatch(@PathVariable("matchId") String matchId) {
        MatchDocument match = matchService.get(matchId).orElse(null);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/matches", method = RequestMethod.GET)
    @ApiOperation(value = "Get matches list", notes = "Return all matches list by league id")
    public ResponseEntity<List<MatchDocument>> get(@PathVariable String leagueId) {
        Sort sortByDate = SortUtils.getSortDescending();
        List<MatchDocument> matches = matchService.findByLeagueId(leagueId, sortByDate);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/completed-matches", method = RequestMethod.GET)
    @ApiOperation(value = "Get completed matches page",
                notes = "Return page with completed matches")
    public ResponseEntity<Page<MatchDocument>> getCompleted(@PathVariable String leagueId,
                                                            @RequestParam int page,
                                                            @RequestParam(defaultValue = "10") int pageSize,
                                                            @RequestParam(required = false) String sort) {
        Sort sortByDate = SortUtils.getSort(sort);
        PageRequest pageRequest = PageRequest.of(page, pageSize, sortByDate);
        Page<MatchDocument> matches = matchService.findByLeagueIdAndCompletedIsTrue(leagueId, pageRequest);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/scheduled-matches", method = RequestMethod.GET)
    @ApiOperation(value = "Get scheduled matches page",
                notes = "Return page with scheduled matches")
    public ResponseEntity<List<MatchDocument>> getScheduled(@PathVariable String leagueId,
                                                            @RequestParam(required = false) String sort) {
        Sort sortByDate = SortUtils.getSort(sort);
        List<MatchDocument> matches = matchService.findByLeagueIdAndCompletedIsFalse(leagueId, sortByDate);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/league/{leagueId}/reschedule-matches/{minutes}", method = RequestMethod.POST)
    @ApiOperation(value = "Reschedule scheduled matches by {minutes} defined in request",
        notes = "Return page with rescheduled matches")
    public ResponseEntity<List<MatchDocument>> rescheduleMatches(HttpServletRequest request,
                                                                 @PathVariable String leagueId,
                                                                 @PathVariable int minutes,
                                                                 @RequestParam(required = false) String sort) {
        Sort sortByDate = SortUtils.getSort(sort);
        String originUrl = getOriginUrl(request);
        List<MatchDocument> matches = matchService.rescheduleMatchesInLeague(leagueId, minutes, sortByDate, originUrl);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }


    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/matches", method = RequestMethod.POST)
    @ApiOperation(value = "Create match", notes = "Create new match")
    public ResponseEntity<MatchDocument> save(HttpServletRequest request, @PathVariable String leagueId, @RequestBody MatchDocument match) {
        match.setLeague(new LeagueDocument(leagueId));
        if (matchService.checkIfCompleted(match))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else if (match.isCompleted())
            match = matchService.saveMatchWithPlayers(match);
        else
            match = matchService.saveAndNotify(match, getOriginUrl(request));
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/matches/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete match", notes = "Delete match by match id")
    public ResponseEntity<MatchDocument> delete(HttpServletRequest request, @PathVariable String id) {
        matchService.deleteByIdWithNotification(id, getOriginUrl(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/matches/{id}/revert", method = RequestMethod.POST)
    @ApiOperation(value = "Revert match",
                notes = "Delete match and revert players rating to previous state")
    public ResponseEntity<MatchDocument> revert(@PathVariable String id) {
        matchService.get(id).ifPresent(match -> {
            matchService.delete(match.getId());
            playerService.restorePlayers(match);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getOriginUrl(HttpServletRequest request) {
        return request.getHeader("Origin");
    }

}
