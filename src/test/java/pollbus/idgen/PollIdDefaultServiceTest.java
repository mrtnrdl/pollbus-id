package pollbus.idgen;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import io.baratine.core.Lookup;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.caucho.junit.ConfigurationBaratine;
import com.caucho.junit.RunnerBaratine;

@RunWith(RunnerBaratine.class)
@ConfigurationBaratine(services = { IdGeneratorDefaultService.class }, pod = "a")
public class PollIdDefaultServiceTest {

	@Inject
	@Lookup("pod://a/id-generator")
	private IdGeneratorSync idGen;

	@Test
	public void podlocalEndpointBootstrappedByLookup() {
		assertThat(idGen.nextId(), notNullValue());
	}

	// TODO: use async api calls for those kind of tests
	// long start;
	// long end;
	// @Test
	// public void assign100kIdsSingleClient() {
	// start = System.currentTimeMillis();
	// runNextId(1_000_000);
	// end = System.currentTimeMillis();
	//
	// long dt = end - start;
	// assertTrue("Should be below 6 seconds. was: " + dt, dt < 6000);
	// System.out.println("time spent: " + dt);
	// System.out.println("req-ps: " + 1_000_000/((double)dt/1000));
	//
	// }
	//
	// private void runNextId(int times) {
	// for(int i = 0; i < times; i++)
	// idGen.nextId();
	// }

}
