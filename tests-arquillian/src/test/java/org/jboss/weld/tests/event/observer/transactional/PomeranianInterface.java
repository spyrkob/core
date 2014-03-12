/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.tests.event.observer.transactional;

import javax.ejb.Local;

@Local
public interface PomeranianInterface {
    /**
     * Observes a String event only after the transaction is completed.
     *
     * @param someEvent
     */
    void observeInProgress(Bark event);

    /**
     * Observes an Integer event if the transaction is successfully completed.
     *
     * @param event
     */
    void observeAfterCompletion(Bark event);

    /**
     * Observes a Float event only if the transaction failed.
     *
     * @param event
     */
    void observeAfterSuccess(Bark event);

    void observeAfterFailure(Bark event);

    void observeBeforeCompletion(Bark event);
    
    void observeAndFail(Bark event) throws FooException;
}