package xyz.auriium.mattlib2.loop;

import xyz.auriium.yuukonstants.exception.ExplainedException;

public interface Outcome<T> {

    Type type();
    T output();
    ExplainedException[] problems();

    static <T> Outcome<T> fail(ExplainedException... failures) {
        return new Fail<>(failures);
    }

    static <T> Outcome<T> working() {
        return new Working<>();
    }

    static <T> Outcome<T> success(T result) {
        return new Success<>(result);
    }

    static Outcome<Void> success() {
        return new Success<>(null);
    }

    /**
     * The outcome of a routine describes what the routine thinks the state of whatever it is trying to accomplish is
     * used by the robot brain or controlling routine to determine what to do next
     */
    enum Type {
        WORKING, //tells the brain this routine is still working on it's goal
        SUCCESS, //tells the brain that this routine has successfully completed its order
        FAIL, //tells the routine that something caused this routine to fail //i.e. something gets in the way of an arm
    }

    class Success<T> implements Outcome<T> {

        final T output;

        public Success(T output) {
            this.output = output;
        }

        @Override public Type type() {
            return Type.SUCCESS;
        }

        @Override public T output() {
            return output;
        }

        @Override public ExplainedException[] problems() {
            throw new IllegalStateException("cannot call problems on a success");
        }
    }

    class Working<T> implements Outcome<T> {

        @Override public Type type() {
            return Type.WORKING;
        }

        @Override public T output() {
            throw new IllegalStateException("can't use output when routine isn't finished");
        }

        @Override public ExplainedException[] problems() {
            throw new IllegalStateException("can't use problems on working");
        }
    }

    class Fail<T> implements Outcome<T> {

        final ExplainedException[] problems;
        Fail(ExplainedException[] problems) {
            this.problems = problems;
        }

        @Override public Type type() {
            return Type.FAIL;
        }

        @Override public T output() {
            throw new IllegalStateException("can't use output on a failure");
        }

        @Override public ExplainedException[] problems() {
            return problems;
        }
    }


}
