package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.Mattlib;
import yuukonstants.GenericPath;

public interface PIDComponent extends CommonPIDComponent, IndividualPIDComponent {


    /**
     * use this instead of loading a motor component
     * @param path
     * @return
     */
    static PIDComponent workaround(String path) {
        return ofSpecific(
                Mattlib.LOG.load(CommonPIDComponent.class, path),
                Mattlib.LOG.load(IndividualPIDComponent.class, path)
        );
    }
    /**
     * Use this when you want to reuse the common pid parameters (p,i,d) but not the special logging stuff
     * @param pidComponent
     * @param individualPIDComponent
     * @return
     */
    static PIDComponent ofSpecific(CommonPIDComponent pidComponent, IndividualPIDComponent individualPIDComponent) {
        return new Impl(pidComponent, individualPIDComponent);
    }

    /**
     * @param common
     * @param individuals
     * @return an array of pidcomponents indexed by the respective individual pid component indices, all supplied by a common pidcomponent
     */
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
        public GenericPath selfPath() {
            return individualPIDComponent.selfPath();
        }

        @Override
        
        public void reportError(double error) {
            individualPIDComponent.reportError(error);
        }

        @Override
        
        public void reportOutput(double output) {
            individualPIDComponent.reportOutput(output);
        }

        @Override
        public int pConstant() {
            return commonPIDComponent.pConstant();
        }

        @Override
        public int dConstant() {
            return commonPIDComponent.dConstant();
        }

        @Override

        public int iConstant() {
            return commonPIDComponent.iConstant();
        }

        @Override
        public boolean hasUpdated() {
            return commonPIDComponent.hasUpdated();
        }
    }



}
