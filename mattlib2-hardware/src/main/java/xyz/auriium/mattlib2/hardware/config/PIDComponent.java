package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.annotation.HasUpdated;
import xyz.auriium.mattlib2.log.annotation.Log;
import xyz.auriium.mattlib2.log.annotation.SelfPath;
import xyz.auriium.mattlib2.log.annotation.Tune;
import xyz.auriium.mattlib2.log.INetworkedComponent;
import yuukonfig.core.annotate.Key;

public interface PIDComponent extends CommonPIDComponent, IndividualPIDComponent {


    /**
     * Use this when you want to reuse the common pid parameters (p,i,d) but not the special logging stuff
     * @param pidComponent
     * @param individualPIDComponent
     * @return
     */
    static PIDComponent ofSpecific(CommonPIDComponent pidComponent, IndividualPIDComponent individualPIDComponent) {
        return new Impl(pidComponent, individualPIDComponent);
    }

    static PIDComponent[] ofRange(CommonPIDComponent common, IndividualPIDComponent[] individuals) {
        PIDComponent[] motorComponents = new PIDComponent[individuals.length];

        for (int i = 0; i < individuals.length; i++) {
            motorComponents[i] = ofSpecific(common, individuals[i]);
        }

        return motorComponents;
    }


    class Impl implements PIDComponent {
        final CommonPIDComponent commonPIDComponent;
        final IndividualPIDComponent individualPIDComponent;

        Impl(CommonPIDComponent commonPIDComponent, IndividualPIDComponent individualPIDComponent) {
            this.commonPIDComponent = commonPIDComponent;
            this.individualPIDComponent = individualPIDComponent;
        }

        @Override
        @SelfPath
        public String selfPath() {
            return individualPIDComponent.selfPath();
        }

        @Override
        @Log
        public void reportError(double error) {
            individualPIDComponent.reportError(error);
        }

        @Override
        @Log
        public void reportOutput(double output) {
            individualPIDComponent.reportOutput(output);
        }

        @Override
        @Key("p")
        @Tune
        public int pConstant() {
            return commonPIDComponent.pConstant();
        }

        @Override
        @Key("d")
        @Tune
        public int dConstant() {
            return commonPIDComponent.dConstant();
        }

        @Override
        @Key("i")
        @Tune
        public int iConstant() {
            return commonPIDComponent.iConstant();
        }

        @Override
        @HasUpdated(keysToCheck = {"p", "i", "d"})
        public boolean hasUpdated() {
            return commonPIDComponent.hasUpdated();
        }
    }



}
