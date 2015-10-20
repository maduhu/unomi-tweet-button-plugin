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

import org.oasis_open.contextserver.api.Event;
import org.oasis_open.contextserver.api.Metadata;
import org.oasis_open.contextserver.api.Profile;
import org.oasis_open.contextserver.api.PropertyType;
import org.oasis_open.contextserver.api.actions.Action;
import org.oasis_open.contextserver.api.actions.ActionExecutor;
import org.oasis_open.contextserver.api.services.EventService;
import org.oasis_open.contextserver.api.services.ProfileService;

/**
 * Increments the number of times the user associated with the profile tweeted.
 */
public class IncrementTweetNumberAction implements ActionExecutor {
    private static final String TWEET_NB_PROPERTY = "tweetNb";
    private ProfileService service;

    public int execute(Action action, Event event) {
        final Profile profile = event.getProfile();
        final Integer tweetNb = (Integer) profile.getProperty(TWEET_NB_PROPERTY);

        if (tweetNb == null) {
            // check if the property type exists
            PropertyType propertyType = service.getPropertyType(TWEET_NB_PROPERTY);
            if (propertyType == null) {
                // create it
                propertyType = new PropertyType(new Metadata(event.getScope(), TWEET_NB_PROPERTY, TWEET_NB_PROPERTY, "Number of times a user tweeted"));
                propertyType.setValueTypeId("integer");
                service.createPropertyType(propertyType);
            }

            profile.setProperty(TWEET_NB_PROPERTY, 1);

        } else {
            profile.setProperty(TWEET_NB_PROPERTY, tweetNb + 1);
        }

        return EventService.PROFILE_UPDATED;
    }

    public void setProfileService(ProfileService service) {
        this.service = service;
    }
}
