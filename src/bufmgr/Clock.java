package bufmgr;

import global.GlobalConst;
import global.PageId;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Clock implements GlobalConst {

	protected HashMap<PageId, FrameDesc> pageFrameMap;
	protected FrameDesc current;
	 
	public Clock(BufMgr bm) {

		this.pageFrameMap = bm.pageFrameMap;
		this.current = null;
	}
	
	
	public FrameDesc pickVictim() {

		int count = 0;
		//(frametab.length)*2: as we need to check the buff. pool 2 times
		while (count < 2) {
			Iterator map = pageFrameMap.entrySet().iterator();
			while (map.hasNext()) {
				Map.Entry pair = (Map.Entry) map.next();
				PageId key = (PageId) pair.getKey();
				FrameDesc frame = (FrameDesc) pair.getValue();

				//1. if data in bufpool[current] is not valid, choose current
				if (!frame.valid) {
					current = frame;
					return frame;
				}
				//2. if frametab[current]'s pin count is 0
				else if (frame.pin_count == 0) {
					// check if frametab [current] has refbit
					if (frame.refbit) {
						frame.refbit = false;
					} else {
						current = frame;
						return frame;
					}
				}
				// increment current, mod
				count++;
			}
		}
		
		// (-1) if No frame available in the buff. pool
		// TODO: return an error
		return null;
	}
}