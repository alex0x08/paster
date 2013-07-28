/**
 * Copyright (C) 2011 Alex <alex@0x08.tk>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uber.megashare.web;

public class StatsBean {

private final static long MEGABYTE = 1024L * 1024L;


public long getTotalMemory() {
	// Get current size of heap in bytes
	long heapSize = Runtime.getRuntime().totalMemory();
return heapSize / MEGABYTE ;
}

public long getHeapMaxSize() {
	// Get maximum size of heap in bytes. The heap cannot grow beyond this 
	//size.
	// Any attempt will result in an OutOfMemoryException.
	long heapMaxSize = Runtime.getRuntime().maxMemory();
return heapMaxSize / MEGABYTE ;
}

public long getHeapFreeSize() {
	// Get amount of free memory within the heap in bytes. This size will
	// increase
	// after garbage collection and decrease as new objects are created.
	long heapFreeSize = Runtime.getRuntime().freeMemory();
return heapFreeSize / MEGABYTE ;
}
}
