public class HTTP {

    private boolean isRequest;
    private String func;
    private double ver;
    private String html;

    public HTTP(boolean request, String function, double version, String html) {
        isRequest = request;
        func = function;
        ver = version;
        this.html = html;
    }

    public String getRequest(){
        return null;
    }

    public String getResponse(){
        return null;
    }
}
