package com.elorating.player;

import com.elorating.match.Elo;
import com.elorating.match.MatchDocument;
import com.elorating.match.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
class PlayerMatchesServiceImpl implements PlayerMatchesService {

    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    @Autowired
    public PlayerMatchesServiceImpl(PlayerRepository playerRepository,
                                    MatchRepository matchRepository) {
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public List<MatchDocument> findByPlayerId(String playerId, Sort sort) {
        return matchRepository.findByPlayerId(playerId, sort);
    }

    @Override
    public List<MatchDocument> findScheduledByPlayerId(String playerId, Sort sort) {
        return matchRepository.findScheduledByPlayerId(playerId, sort);
    }

    @Override
    public Page<MatchDocument> findCompletedByPlayerId(String playerId, PageRequest pageRequest) {
        return matchRepository.findCompletedByPlayerId(playerId, pageRequest);
    }

    @Override
    public List<MatchDocument> findCompletedByPlayerId(String playerId, Sort sort) {
        return matchRepository.findCompletedByPlayerId(playerId, sort);
    }

    @Override
    public List<MatchDocument> findCompletedByPlayerIds(String playerId, String opponentId, Sort sort) {
        return matchRepository.findCompletedByPlayerIds(playerId, opponentId, sort);
    }

    @Override
    public Page<MatchDocument> findCompletedByPlayerIds(String playerId, String opponentId, PageRequest pageRequest) {
        return matchRepository.findCompletedByPlayerIds(playerId, opponentId, pageRequest);
    }

    @Override
    public List<MatchDocument> findCompletedByPlayerIdAndDate(String playerId, Date from, Sort sort) {
        return matchRepository.findCompletedByPlayerIdAndDate(playerId, from, sort);
    }

    @Override
    public List<MatchDocument> findCompletedByPlayerIdAndDate(String playerId, Date from, Date to, Sort sort) {
        return matchRepository.findCompletedByPlayerIdAndDate(playerId, from, to, sort);
    }

    @Override
    public List<MatchDocument> getMatchForecast(String playerId, String opponentId) {
        PlayerDocument player = playerRepository.findById(playerId).orElseGet(PlayerDocument::new);
        PlayerDocument opponent = playerRepository.findById(opponentId).orElseGet(PlayerDocument::new);
        return generateForecastMatches(player, opponent);
    }

    private List<MatchDocument> generateForecastMatches(PlayerDocument player, PlayerDocument opponent) {
        int maxScore = player.getLeague().getSettings().getMaxScore();
        boolean allowDraws = player.getLeague().getSettings().isAllowDraws();
        List<MatchDocument> matches = new ArrayList<>();
        Elo elo = new Elo(new MatchDocument(player, opponent, maxScore, 0));
        matches.add(elo.getMatch());
        elo = new Elo(new MatchDocument(player, opponent, maxScore, maxScore - 1));
        matches.add(elo.getMatch());
        if (allowDraws) {
            elo = new Elo(new MatchDocument(player, opponent, 0, 0));
            matches.add(elo.getMatch());
        }
        elo = new Elo(new MatchDocument(player, opponent, maxScore - 1, maxScore));
        matches.add(elo.getMatch());
        elo = new Elo(new MatchDocument(player, opponent, 0, maxScore));
        matches.add(elo.getMatch());
        return matches;
    }
}
