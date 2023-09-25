package xyz.auriium.mattlib2.foxglove;

import xyz.auriium.mattlib2.ILogFeature;
import xyz.auriium.mattlib2.ITuneFeature;
import xyz.auriium.mattlib2.ProcessPath;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FoxgloveFeature implements ILogFeature, ITuneFeature {
    @Override
    public void init() {

    }

    @Override
    public <T> Optional<Consumer<T>> generateLogger(ProcessPath path, Class<T> type) {
        return null;
    }

    @Override
    public <T> Optional<Supplier<T>> generateTuner(ProcessPath path, T defaultValue) {
        return Optional.empty();
    }

    @Override
    public void ready() {
        new MattlogWebsocketServer();
    }
}
