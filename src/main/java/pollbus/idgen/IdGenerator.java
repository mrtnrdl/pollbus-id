package pollbus.idgen;

import io.baratine.core.Result;

public interface IdGenerator {

	void nextId(Result<Long> result);

}