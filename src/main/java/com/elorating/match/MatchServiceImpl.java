package com.elorating.match;

import com.elorating.common.AbstractCrudService;
import com.elorating.email.EmailGenerator;
import com.elorating.email.EmailService;
import com.elorating.player.Player;
import com.elorating.player.PlayerRepository;
import com.elorating.web.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
class MatchServiceImpl extends AbstractCrudService<Match, MatchRepository> implements MatchService {

    private final PlayerRepository playerRepository;
    private final EmailService emailService;
    private final EmailGenerator emailGenerator;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository, PlayerRepository playerRepository,
                            EmailService emailService, EmailGenerator emailGenerator) {
        super(matchRepository);
        this.playerRepository = playerRepository;
        this.emailService = emailService;
        this.emailGenerator = emailGenerator;
    }

    @Override
    public List<Match> saveAndNotify(List<Match> matches, String originUrl) {
        for (Match match : matches) {
            saveAndNotify(match, originUrl);
        }
        return matches;
    }

    @Override
    public Match saveAndNotify(Match match, String originUrl) {
        boolean update = checkIfMatchToUpdate(match);
        match = save(match);
        match = fulfillPlayersInfo(match);
        if (update) {
            this.emailService.sendEmails(emailGenerator.generateEmails(match, emailGenerator.EDIT_MATCH, originUrl));
        } else {
            this.emailService.sendEmails(emailGenerator.generateEmails(match, emailGenerator.SCHEDULE_MATCH, originUrl));
        }
        return match;
    }

    @Override
    public void deleteByIdWithNotification(String id, String originUrl) {
        repository.findById(id).ifPresent(matchToDelete -> {
            repository.deleteById(id);
            matchToDelete = fulfillPlayersInfo(matchToDelete);
            if (!repository.findById(id).isPresent()) {
                this.emailService.sendEmails(emailGenerator.generateEmails(matchToDelete, emailGenerator.CANCEL_MATCH, originUrl));
            }
        });
    }

    @Override
    public Match saveMatchWithPlayers(Match match) {
        Elo elo = new Elo(match);
        match.getPlayerOne().setRating(elo.getPlayerOneRating());
        match.getPlayerTwo().setRating(elo.getPlayerTwoRating());
        match.setRatingDelta(elo.getMatch().getRatingDelta());
        updatePlayer(match.getPlayerOne(), match.getWinnerId());
        updatePlayer(match.getPlayerTwo(), match.getWinnerId());
        match.setCompleted();
        match.setDate(new Date());
        return save(match);
    }

    private void updatePlayer(Player player, String winnerId) {
        playerRepository.findById(player.getId()).ifPresent(playerToUpdate -> {
            playerToUpdate.setRating(player.getRating());
            playerToUpdate.updateStatistics(winnerId);
            playerRepository.save(playerToUpdate);
        });
    }

    @Override
    public List<Match> findByLeagueId(String leagueId, Sort sortByDate) {
        return repository.findByLeagueId(leagueId, sortByDate);
    }

    @Override
    public Page<Match> findByLeagueIdAndCompletedIsTrue(String leagueId, Pageable pageRequest) {
        return repository.findByLeagueIdAndCompletedIsTrue(leagueId, pageRequest);
    }

    @Override
    public List<Match> findByLeagueIdAndCompletedIsFalse(String leagueId, Sort sortByDate) {
        return repository.findByLeagueIdAndCompletedIsFalse(leagueId, sortByDate);
    }

    @Override
    public List<Match> findByPlayerId(String playerId) {
        return repository.findByPlayerId(playerId);
    }

    @Override
    public List<Match> findByCompletedIsFalse() {
        return repository.findByCompletedIsFalse();
    }

    @Override
    public List<Match> rescheduleMatchesInLeague(String leagueId, int minutes,
                                                 Sort sort, String originUrl) {
        List<Match> matchesInQueue = repository.findByLeagueIdAndCompletedIsFalse(leagueId, sort);
        List<Match> matchesToReschedule = new ArrayList<>(matchesInQueue.size());

        Date rescheduleTime = new Date();
        for (int i = 0; i < matchesInQueue.size(); i++) {
            Match match = matchesInQueue.get(i);
            if (i == 0 && match.getDate().getTime() <= rescheduleTime.getTime()) {
                match.setDate(DateUtils.adjustTimeByMinutesIntoFuture(match.getDate(), minutes));
                matchesToReschedule.add(match);
            } else {
                rescheduleTime = DateUtils.adjustTimeByMinutesIntoFuture(matchesInQueue.get(i - 1).getDate(), minutes);
                if (match.getDate().getTime() < rescheduleTime.getTime()) {
                    match.setDate(rescheduleTime);
                    matchesToReschedule.add(match);
                }
            }
        }

        saveAndNotify(matchesToReschedule, originUrl);
        return repository.findByLeagueIdAndCompletedIsFalse(leagueId, sort);
    }

    @Override
    public boolean checkIfCompleted(Match match) {
        if (match.getId() != null && match.getId().length() > 0) {
            Match matchToCheck = repository.findByIdAndCompletedIsTrue(match.getId());
            return matchToCheck != null;
        }
        return false;
    }

    private boolean checkIfMatchToUpdate(Match match) {
        return match.getId() != null ? true : false;
    }

    private Match fulfillPlayersInfo(Match match) {
        Optional<Player> playerOne = playerRepository.findById(match.getPlayerOne().getId());
        match.setPlayerOne(playerOne.orElseGet(Player::new));
        Optional<Player> playerTwo = playerRepository.findById(match.getPlayerTwo().getId());
        match.setPlayerTwo(playerTwo.orElseGet(Player::new));
        return match;
    }
}