import xyz.auriium.mattlib2.foxe.FoxgloveFeature;

public class FoxgloveFeatureScratchpad {

    public static void main(String[] args) {

        try (var ff = new FoxgloveFeature()) {
            ff.init();
            ff.ready();

            while (true) {

            }
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }




    }

}
