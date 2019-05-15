package com.elorating.email.builder;

import com.elorating.email.EmailBuilder;
import com.elorating.email.EmailStrings;
import com.elorating.league.LeagueDocument;
import org.thymeleaf.context.Context;

public class EditMatchEmail extends EmailBuilder {

    private String opponent;
    private String matchtime;
    private LeagueDocument league;

    public EditMatchEmail(String opponent, String recipient, String matchtime, String originUrl, LeagueDocument league) {
        this.opponent = opponent;
        this.recipient = recipient;
        this.matchtime = matchtime;
        this.originUrl = originUrl;
        this.league = league;
    }

    @Override
    public void buildRecipient() {
        email.setRecipient(recipient);
    }

    @Override
    public void buildSubject() {
        email.setSubject(EmailStrings.EDITED_MATCH);
    }

    @Override
    public void buildTemplateName() {
        email.setTemplateName(EmailStrings.EDITED_MATCH_TEMPLATE);
    }

    @Override
    public void buildContext() {
        Context context = email.getContext();
        String redirectUrl = originUrl + "/leagues/" + this.league.getId() + "/matches";
        context.setVariable("redirectUrl", redirectUrl);
        context.setVariable("opponent", opponent);
        context.setVariable("matchtime", matchtime);
    }
}
