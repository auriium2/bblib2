package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annote.Log;

/**
 * Often very specific details of a pid controller
 */
public interface IndividualPIDComponent extends INetworkedComponent {

    /**
     * The reference (u or control) value that the PID controller is currently operating with
     * @param reference_primeUnits a reference value in "prime units", arbitrary number that is only guarunteed to be
     *                             the same as the units used by the state report
     */
    @Log("reference") void reportReference(double reference_primeUnits);

    /**
     * The state (x or state) value that the PID controller is operating with
     * @param state_primeUnits a reference value in "prime units" arbitrary number that is only guarunteed to be the same as units used by state report
     */
    @Log("state") void reportState(double state_primeUnits);
    @Log("iteration") void reportIteration(int id);
    @Log("atGoal") void reportAtGoal(boolean atGoal);

}
