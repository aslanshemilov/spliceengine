/*
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Some parts of this source code are based on Apache Derby, and the following notices apply to
 * Apache Derby:
 *
 * Apache Derby is a subproject of the Apache DB project, and is licensed under
 * the Apache License, Version 2.0 (the "License"); you may not use these files
 * except in compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Splice Machine, Inc. has modified the Apache Derby code in this file.
 *
 * All such Splice Machine modifications are Copyright 2012 - 2020 Splice Machine, Inc.,
 * and are licensed to you under the GNU Affero General Public License.
 */

package com.splicemachine.db.impl.services.locks;

import java.util.Map;
import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.services.locks.CompatibilitySpace;
import com.splicemachine.db.iapi.services.locks.Latch;
import com.splicemachine.db.iapi.services.locks.Lockable;

/**
 * Interface which must be implemented by lock table classes.
 */
interface LockTable {

    /**
     * Lock an object.
     *
     * @param compatibilitySpace the compatibility space
     * @param ref the object to lock
     * @param qualifier qualifier of the lock
     * @param timeout maximum time to wait in milliseconds
     * ({@code C_LockFactory.NO_WAIT} means don't wait)
     * @return a reference to the lock, or <code>null</code> if the lock
     * couldn't be obtained immediately and the timeout was {@code NO_WAIT}
     * or {@code LockOwner} had the {@code noWait} flag set
     * @exception StandardException if the lock could not be obtained
     */
    Lock lockObject(CompatibilitySpace compatibilitySpace, Lockable ref,
                    Object qualifier, int timeout)
            throws StandardException;

    /**
     * Unlock an object previously locked by <code>lockObject()</code>.
     *
     * @param item the lock to unlock
     * @param unlockCount the number of times to unlock the item; or zero,
     * meaning take the unlock count from the item
     */
    void unlock(Latch item, int unlockCount);

    /**
     * Unlock an object once if it is present in the specified group. Also
     * remove the object from the group.
     *
     * @param space the compatibility space
     * @param ref a reference to the locked object
     * @param qualifier qualifier of the lock
     * @param group a map representing the locks in a group
     * @return the corresponding lock in the group map, or <code>null</code> if
     * the object was not unlocked
     */
    Lock unlockReference(CompatibilitySpace space, Lockable ref,
                         Object qualifier, Map group);

    /**
     * Notify the lock table that it has one more waiter.
     */
    void oneMoreWaiter();

    /**
     * Notify the lock table that it has one less waiter.
     *
     */
    void oneLessWaiter();

    /**
     * Check whether there are anyone blocked in the lock table.
     *
     * @return <code>true</code> if someone is blocked, <code>false</code>
     * otherwise
     */
    boolean anyoneBlocked();

    /**
     * Lock an object and release the lock immediately. Equivalent to
     * <pre>
     * Lock lock = lockTable.lockObject(space, ref, qualifier, timeout);
     * lockTable.unlock(lock, 1);
     * </pre>
     * except that the implementation might be more efficient.
     *
     * @param space the compatibility space
     * @param ref a reference to the locked object
     * @param qualifier qualifier of the lock
     * @param timeout maximum time to wait in milliseconds
     * ({@code C_LockFactory.NO_WAIT} means don't wait)
     * @return <code>true</code> if the object was locked, or
     * {@code false} if the object couldn't be locked immediately and timeout
     * was {@code NO_WAIT} or {@code LockOwner} had the {@code noWait} flag set
     * @exception StandardException if the lock could not be obtained
     */
    boolean zeroDurationLockObject(CompatibilitySpace space, Lockable ref,
                                   Object qualifier, int timeout)
            throws StandardException;

    /**
     * Create a map containing a snapshot of all the (<code>Lockable</code>,
     * <code>LockControl</code>) pairs in the lock table.
     *
     * @return a shallow clone of the lock table
     */
    Map shallowClone();

    /**
     * Set the deadlock timeout.
     *
     * @param timeout deadlock timeout in milliseconds
     */
    void setDeadlockTimeout(int timeout);

    /**
     * Set the wait timeout.
     *
     * @param timeout wait timeout in milliseconds
     */
    void setWaitTimeout(int timeout);

    /**
     * Get the wait timeout in milliseconds.
     */
    int getWaitTimeout();

    /**
     * Enable or disable tracing of deadlocks.
     *
     * @param flag <code>true</code> enables tracing, <code>false</code>
     * disables tracing
     */
    void setDeadlockTrace(boolean flag);

    /**
     * Add all waiters in the lock table to the specified map.
     *
     * @param waiters the map to add the waiters to
     * @see LockControl#addWaiters
     */
    void addWaiters(Map waiters);
}
