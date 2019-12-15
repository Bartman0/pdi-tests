package nl.inergy.pdi.unittest.db;

import nl.inergy.pdi.unittest.util.Ssm;

public class Credentials {

    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public Credentials() {}

    public Credentials(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public void set(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public Credentials initContinuousIntegrationAWS(String database) {
        String environment = Ssm.getParameter("/pnl/led/dwh/CI/environment");
        dbUrl = Ssm.getParameter(String.format("/pnl%s/led/dwh/CI/database/%s/url", environment, database));
        dbUser = Ssm.getParameter(String.format("/pnl%s/led/dwh/CI/database/%s/user", environment, database));
        dbPassword = Ssm.getParameter(String.format("/pnl%s/led/dwh/CI/database/%s/password", environment, database));
        return this;
    }
}
