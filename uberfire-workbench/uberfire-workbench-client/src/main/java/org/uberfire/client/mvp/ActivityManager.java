/*
 * Copyright 2012 JBoss Inc
 *
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
 */
package org.uberfire.client.mvp;

import java.util.Set;

import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.authz.AuthorizationManager;

/**
 * A facility for obtaining references to specific {@link Activity} instances and for enumerating or discovering all
 * available activities of a certain type (screens, editors, popup dialogs, and so on). Also responsible for shutting
 * down and releasing Activity instances when they are no longer needed.
 * <p>
 * Note that you may never need to use an ActivityManager. While used extensively within the framework, UberFire
 * application code rarely comes into direct contact with activities, which are essentially autogenerated wrappers
 * around classes annotated with {@link WorkbenchScreen}, {@link WorkbenchEditor}, {@link WorkbenchPopup}, and friends.
 * Most Activity-related tasks can be accomplished at arm's length through a {@link PlaceManager}.
 * <p>
 * If you do need an instance of ActivityManager in your application, obtain it using {@code @Inject}.
 *
 * @see PlaceManager
 * @see Activity
 */
public interface ActivityManager {

    /**
     * Obtains the set of activity instances which implement the given type, are {@link ActivatedBy active}, and that
     * the current user {@link AuthorizationManager has permission to access}.
     *
     * @param abstractScreenActivityClass
     *            the type of activities to enumerate. Must not be null. Passing in {@code Activity.class} will yield
     *            all possible activity types.
     * @return the set of available activities. Never null. Each object in the returned set must be freed by the caller
     *         via a call to {@link SyncBeanManager#destroyBean(Object)}.
     * @deprecated this method returns Activity instances that have not had their onStartup() methods invoked, so they
     *             can not be displayed according to the normal Activity lifecycle. It is also up to the caller to free
     *             each of the returned Activity instances by calling {@link SyncBeanManager#destroyBean(Object)} on
     *             them. Consider using the Errai bean manager and UberFire AuthorizationManager directly instead of
     *             using this method. See UF-105 for details.
     */
    @Deprecated
    <T extends Activity> Set<T> getActivities( final Class<T> abstractScreenActivityClass );

    /**
     * Returns the splash screen activity that should appear upon navigation to the given place, if such a splash screen
     * exists. In case multiple splash screens would intercept the given place request, one of them is chosen at random.
     * TODO (UF-93) : make this deterministic.
     *
     * @param placeRequest
     *            the place request to look up a splash screen for.
     * @return a splash screen which should be displayed upon navigation to the given place, or null if no such
     *         activity exists.
     */
    SplashScreenActivity getSplashScreenInterceptor( PlaceRequest placeRequest );

    /**
     * Returns the set of activities that can handle the given PlaceRequest. The activities will be in the
     * <i>started</i> state (see {@link Activity} for details on the activity lifecycle). If the PlaceRequest is for a
     * certain place ID, this method will return a set with at most one activity in it. If the PlaceRequest is for a
     * certain path, the returned set can contain any number of activities.
     *
     * @param placeRequest
     *            the PlaceRequest to resolve activities for. Although null is permitted for convenience, it always
     *            resolves to the empty set.
     * @return an unmodifiable set of activities that can handle the given PlaceRequest. Never null, but can be empty.
     *         To prevent memory leaks, pass Activity in the returned set to {@link #destroyActivity(Activity)} when you
     *         are done with it.
     */
    Set<Activity> getActivities( final PlaceRequest placeRequest );

    /**
     * Returns an active, accessible activity that can handle the given PlaceRequest. In case there are multiple
     * activities that can handle the given place request, one of them is chosen at random. TODO (UF-92) : make this
     * deterministic.
     *
     * @param clazz
     *            the type of activity that you expect to find.
     * @param placeRequest
     * @return an activity that handles the given PlaceRequest, or null if no available activity can handle. <b>No
     *         actual type checking is performed! If you guess the type wrong, you will have an instance of the
     *         wrong type. The only truly "safe" type to guess is {@link Activity}.</b>.
     */
    boolean containsActivity( final PlaceRequest placeRequest );

    /**
     * Finds an activity that can handle the given PlaceRequest, creating and starting a new one if necessary.
     *
     * @param placeRequest
     *            the place the resolved activity should handle
     * @return an activity that can handle the request, or null if no known activity can handle it. If the return value
     *         is non-null, it will be an activity in the <i>started</i> or <i>open</i> state.
     */
    Activity getActivity( final PlaceRequest placeRequest );

    /**
     * Works like {@link #getActivity(PlaceRequest)} but performs an unsafe cast to treat the return value as an
     * instance of the given class. Only use this method if you are absolutely sure which activity type matches the
     * request. If you are wrong, there will not be a ClassCastException as a result of this call. The safer approach is
     * to use {@link #getActivity(PlaceRequest)} and cast its return value explicitly.
     *
     * @param placeRequest
     *            the place the resolved activity should handle
     * @return an activity that can handle the request, or null if no known activity can handle it. If the return value
     *         is non-null, it will be an activity in the <i>started</i> or <i>open</i> state.
     */
    <T extends Activity> T getActivity( final Class<T> clazz,
                                        final PlaceRequest placeRequest );

    /**
     * Destroys the given Activity bean instance, making it eligible for garbage collection.
     *
     * @param activity
     *            the activity instance to destroy. <b>Warning: do not use with instances of SplashScreenActivity. These
     *            are ApplicationScoped and cannot be destroyed.
     * @throws IllegalArgumentException
     *             if {@code activity} is a SplashScreenActivity. TODO (UF-91) : fix this.
     */
    void destroyActivity( final Activity activity );

}
