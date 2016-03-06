package pollbus.idgen;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static pollbus.idgen.PollbusIdAssert.*;

import org.junit.Test;

// TODO: testcase for setting time backwards 
public class PollbusIdGeneratorTest {

	private BarflakeIdGenerator idGenerator;

	// ================================================================================
	// using defaults
	static final int DATACENTER_ID_ZERO = 0;
	static final int WORKER_ID_ZERO = 0;	
	private final TimeFreezingMillisProvider timerStartingAtZero = new TimeFreezingMillisProvider(0L);
	
	@Test
	public void initialSequenceShouldStartWithBinary1() {
		
		idGenerator = new BarflakeIdGenerator(timerStartingAtZero, DATACENTER_ID_ZERO, WORKER_ID_ZERO);
		long firstValue = idGenerator.nextId();
		assertSequenceBitsAre(firstValue, "000000000001");
		
	}

	// ================================================================================
	// using workerId
	@Test
	public void encodedWorkerIdShouldBeEquallyRepresented() {
		
		idGenerator = new BarflakeIdGenerator(timerStartingAtZero, DATACENTER_ID_ZERO, 1);
		// 00000 00001 000000000001
		long forWorkerIdOne = idGenerator.nextId();
		assertWorkerBitsAre(forWorkerIdOne, "00001");
		
		idGenerator = new BarflakeIdGenerator(timerStartingAtZero, DATACENTER_ID_ZERO, 2);
		// 00000 00010 000000000001
		long two = idGenerator.nextId();		
		assertWorkerBitsAre(two, "00010");

	}

	// ================================================================================
	// using datacenter
	@Test
	public void encodedDatacenterIdShouldBeEquallyRepresented() {
		
		idGenerator = new BarflakeIdGenerator(timerStartingAtZero, 1, WORKER_ID_ZERO);
		long forDatacenterOne = idGenerator.nextId();
		assertDatacenterBitsAre(forDatacenterOne, "00001");

		idGenerator = new BarflakeIdGenerator(timerStartingAtZero, 2, WORKER_ID_ZERO);
		long forDatacenterTwo = idGenerator.nextId();
		assertDatacenterBitsAre(forDatacenterTwo, "00010");
	}
	
	@Test
	public void encodedTimestampIsEquallyRepresented() {
		
		TimeFreezingMillisProvider millisStartedAt1 = new TimeFreezingMillisProvider(1L);
		idGenerator = new BarflakeIdGenerator(millisStartedAt1, DATACENTER_ID_ZERO + 1, WORKER_ID_ZERO +1);
		// expected  1 00001 00001 000000000001
		//           1 00001 00001 000000000001
		long v = idGenerator.nextId();
		System.out.println(Long.toBinaryString(v));
		long t1Expected = sumOfExponentsBase2(0, 12, 17, 22);
		assertThat(v, is(t1Expected));
		
		// expected  10 00001 00001 000000000001
		millisStartedAt1.set(2L);
		long longValueAtT2 = sumOfExponentsBase2(0, 12, 17, 23);
		assertThat(idGenerator.nextId(), is(longValueAtT2));
		
	}
	
	private static final class TimeFreezingMillisProvider implements CurrentTimeMillisProvider {
		
		public TimeFreezingMillisProvider(long initialValue) {
			v = initialValue;
		}
		
		long v;
		
		@Override
		public long currentAppTime() {
			return v;
		}
		
		public void set(long time){
			v = time;
		}
	};

}
