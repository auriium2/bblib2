import xyz.auriium.mattlib2.foxe.FoxgloveMattLog;

public class FoxgloveFeatureScratchpad {

    public static void main(String[] args) {

        try (var ff = new FoxgloveMattLog()) {
            ff.init();
            ff.ready();

            while (true) {

            }
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }




    }

}
