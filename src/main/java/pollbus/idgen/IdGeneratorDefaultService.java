package pollbus.idgen;

import io.baratine.core.OnInit;
import io.baratine.core.Result;
import io.baratine.core.Service;

@Service("public:///id-generator")
public class IdGeneratorDefaultService implements IdGenerator {
	
	private BarflakeIdGenerator generator;
	
	@OnInit
	public void init(Result<Boolean> result) {
		generator = new BarflakeIdGenerator(1, 1);
		result.complete(true);
	}
	
	@Override
	public void nextId(Result<Long> result) {
		result.complete(generator.nextId());
	}

}
