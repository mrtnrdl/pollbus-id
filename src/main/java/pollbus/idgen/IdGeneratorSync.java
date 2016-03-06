package pollbus.idgen;

public interface IdGeneratorSync {

	long nextId() throws InvalidSystemClock;

}
