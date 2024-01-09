package xyz.auriium.mattlib2.hardware.config;

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
        public String selfPath() {
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