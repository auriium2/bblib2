package yuukonfig;



public class ConditionalDouble {

    enum Condition {
        APPA(0),
        MCR(0),
        COMPETITION(0),
        TESTING(0);

        final int priority; //higher is better

        Condition(int priority) {
            this.priority = priority;
        }
    }

    //final double rootValue;


}
