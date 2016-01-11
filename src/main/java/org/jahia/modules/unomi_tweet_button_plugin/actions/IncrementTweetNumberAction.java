package org.jahia.modules.unomi_tweet_button_plugin.actions;

/*
 * #%L
 * Context Server Plugin - Provides request reading actions
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 - 2015 Jahia Solutions
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.oasis_open.contextserver.api.*;
import org.oasis_open.contextserver.api.actions.Action;
import org.oasis_open.contextserver.api.actions.ActionExecutor;
import org.oasis_open.contextserver.api.services.EventService;
import org.oasis_open.contextserver.api.services.ProfileService;

import java.util.*;

/**
 * Increments the number of times the user associated with the profile tweeted.
 */
public class IncrementTweetNumberAction implements ActionExecutor {
    private static final String TWEET_NB_PROPERTY = "tweetNb";
    private static final String TWEETED_FROM_PROPERTY = "tweetedFrom";
    private static final Set<String> tags = new HashSet<>(2);
    private static final String TARGET = "profiles";

    static {
        tags.add("social");
        tags.add("personalProfileProperties");
    }

    private ProfileService service;

    public int execute(Action action, Event event) {
        final Profile profile = event.getProfile();
        Integer tweetNb = (Integer) profile.getProperty(TWEET_NB_PROPERTY);
        List<String> tweetedFrom = (List<String>) profile.getProperty(TWEETED_FROM_PROPERTY);

        if (tweetNb == null || tweetedFrom == null) {
            // create tweet number property type
            PropertyType propertyType = new PropertyType(new Metadata(event.getScope(), TWEET_NB_PROPERTY, TWEET_NB_PROPERTY, "Number of times a user tweeted"));
            propertyType.setValueTypeId("integer");
            propertyType.setTagIds(tags);
            propertyType.setTarget(TARGET);
            service.createPropertyType(propertyType);

            // create tweeted from property type
            propertyType = new PropertyType(new Metadata(event.getScope(), TWEETED_FROM_PROPERTY, TWEETED_FROM_PROPERTY, "The list of pages a user tweeted from"));
            propertyType.setValueTypeId("string");
            propertyType.setTagIds(tags);
            propertyType.setTarget(TARGET);
            propertyType.setMultivalued(true);
            service.createPropertyType(propertyType);

            tweetNb = 0;
            tweetedFrom = new ArrayList<>();
        }

        profile.setProperty(TWEET_NB_PROPERTY, tweetNb + 1);
        final String sourceURL = extractSourceURL(event);
        if (sourceURL != null) {
            tweetedFrom.add(sourceURL);
        }
        profile.setProperty(TWEETED_FROM_PROPERTY, tweetedFrom);

        return EventService.PROFILE_UPDATED;
    }

    public void setProfileService(ProfileService service) {
        this.service = service;
    }

    private String extractSourceURL(Event event) {
        final Item sourceAsItem = event.getSource();
        if (sourceAsItem instanceof CustomItem) {
            CustomItem source = (CustomItem) sourceAsItem;
            final Map<String, Object> pageInfo = (Map<String, Object>) source.getProperties().get("pageInfo");
            if (pageInfo != null) {
                return (String) pageInfo.get("destinationURL");
            }
        }

        return null;
    }
}
