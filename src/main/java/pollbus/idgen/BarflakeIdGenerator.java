package pollbus.idgen;

public class BarflakeIdGenerator implements IdGeneratorSync {
	
	private static final int SEQUENCE_BITS = 12;
	private static final int WORKER_BITS = 5;
	private static final int DATACENTER_BITS = 5;
	
	private static final int LSHIFT_WORKER = SEQUENCE_BITS;
	private static final int LSHIFT_DATACENTER = SEQUENCE_BITS + WORKER_BITS;
	private static final int LSHIFT_TIMESTAMP = SEQUENCE_BITS + WORKER_BITS + DATACENTER_BITS;
	
	private long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);
	
	private final int datacenterId;
	private final int workerId;
	
	private long timestamp;
	private long sequence = 0L;
	private long lastTimestamp = 0L;
	
	private CurrentTimeMillisProvider timer;
	
	
	public BarflakeIdGenerator(CurrentTimeMillisProvider timer, int datacenterId, int workerId) {		
		this.timer = timer;
		this.datacenterId = datacenterId;
		this.workerId = workerId;
	}
	
	public BarflakeIdGenerator(int datacenterId, int workerId) {
		this(new PollbusEpocheTimeProvider(), datacenterId, workerId);
	}
	
	private long binaryConvert(long time, int datacenter, int workerId, long maskedSeq ) {
		
	   long value = ((time) << LSHIFT_TIMESTAMP) | 
	       (datacenterId << LSHIFT_DATACENTER) | 
		   (workerId << LSHIFT_WORKER) |
		   maskedSeq;
	   
		return value;		
	}
	
	@Override
	public long nextId() throws InvalidSystemClock {		
		update();
		return binaryConvert(timestamp, datacenterId, workerId, (sequence & SEQUENCE_MASK));
	}

	private void update() throws InvalidSystemClock {
		
		long newTime = timer.currentAppTime();		
		
		if(newTime < lastTimestamp) {	
			throw new InvalidSystemClock("current time is before last used timestamp!");
			
		} else if (newTime > lastTimestamp) {
			sequence = 1L;
			
		} else if (newTime == lastTimestamp) {
			sequence++;
		}
		
		this.lastTimestamp = timestamp;
		this.timestamp = newTime;			
	}
		
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [datacenterId=" + datacenterId + ", workerId=" + workerId + "]";
	}
	
	
	private static final class PollbusEpocheTimeProvider implements CurrentTimeMillisProvider {
		public static final long POLLBUS_EPOCH = 1456530296738L;
		
		@Override
		public long currentAppTime() {
			return System.currentTimeMillis() - POLLBUS_EPOCH;
		}
	}

}
