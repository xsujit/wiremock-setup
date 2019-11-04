class StubData {
    private String method;
    private String url;
    private String body;
    private int statusCode;

    String getMethod() {
        return method;
    }

    void setMethod(String method) {
        this.method = method;
    }

    String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    String getBody() {
        return body;
    }

    void setBody(String body) {
        this.body = body;
    }

    int getStatusCode() {
        return statusCode;
    }

    void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
