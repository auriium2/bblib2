package xyz.auriium.mattlib2.auto.ff;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annote.Conf;
import xyz.auriium.mattlib2.log.annote.Log;

import java.util.Optional;

public interface GenerateFFComponent extends INetworkedComponent {



    @Conf("startDelay_ms") Optional<Long> delay_ms();
    @Conf("endVoltage") Optional<Double> endVoltage_volts();
    @Conf("rampRate_vPerMs") Optional<Double> rampRate_voltsPerMS();

    @Log("inputVelocity") void logInputVelocity(double iv);
    @Log("inputVoltage") void logInputVoltage(double iV);

    @Log("predictKS") void logPredictedStaticConstant(double ks);
    @Log("predictKV") void logPredictedVelocityConstant(double kv);


}
