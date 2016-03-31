package ix.ncats.moldev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ix.ncats.controllers.App;
import play.Configuration;
import play.db.DB;
import play.mvc.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

public class MoldevApp extends App {
    static Connection hcsConn;
    static ObjectMapper mapper = new ObjectMapper();
    static final String VERSION = "1.0.1";

    static void makeConnection() {
        hcsConn = DB.getConnection("moldev");
    }

    static void closeConnection() throws SQLException {
        hcsConn.close();
    }

    static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    static String getRepositoryPath() {
        return Configuration.root().getString("moldev.repo");
    }

    public static Result info() {
        ObjectNode node = mapper.createObjectNode();
        node.put("version", VERSION);
        node.put("description", "Services to interact with the ImageXpress database and screening data");
        return ok(node);
    }

    public static Result getJobStatus() throws SQLException {
        makeConnection();
        PreparedStatement pst = hcsConn.prepareStatement("select job_id, job_queue.assay_profile_id, status, progress, assay_name, settings_name, plates.plate_name from job_queue, assay_profile, plates " +
                " where job_queue.assay_profile_id = assay_profile.assay_profile_id " +
                " and plates.plate_id = replace(REGEXP_SUBSTR(source_data, '\"[0-9]*\"'), '\"')");
        ResultSet rs = pst.executeQuery();
        StringBuffer sb = new StringBuffer();
        sb.append("<html><body>\n<head>\n<title>PowerCore Job Status</title>\n" +
                "<style type=\"text/css\">\nthead { font-weight: bold; font-family: sans-serif;} \n td { padding-right: 10px; } \n tr.oddrow { background-color: #fff; } tr.evenrow { background-color: #eee; } \n" +
                "</style>" +
                "\n</head>\n<body>\n");
        sb.append("<table><thead><td>Job ID</td><td>Assay Profile ID</td><td>Plate Name</td><td>Assay Details</td><td>Status</td><td>Progress</td></thead>\n<tbody>\n");
        int njob = 0;
        while (rs.next()) {
            if (njob % 2 == 0) sb.append("<tr class='evenrow'>");
            else sb.append("<tr class='oddrow'>");
            sb.append("<td>").append(rs.getInt("job_id")).append("</td>");
            sb.append("<td>").append(rs.getInt("assay_profile_id")).append("</td>");
            sb.append("<td>").append(rs.getString("plate_name")).append("</td>");
            sb.append("<td>").append(rs.getString("assay_name")).append(" [").append(rs.getString("settings_name")).append("] ").append("</td>");
            sb.append("<td>").append(rs.getString("status")).append("</td>");
            sb.append("<td>").append(rs.getString("progress")).append("</td>");
            sb.append("</tr>\n");
            njob++;
        }
        sb.append("</table>\n<br><br><br>\n");

        // see if we have any plates being aquired
        pst.close();
        pst = hcsConn.prepareStatement("select job_id, status, progress,  plates.plate_name from job_queue, plates " +
                " where job_queue.assay_profile_id is null" +
                " and plates.plate_id = replace(REGEXP_SUBSTR(source_data, '\"[0-9]*\"'), '\"')");
        rs = pst.executeQuery();
        sb.append("<table><thead><td>Job ID</td><td>Plate Name</td><td>Status</td><td>Progress</td></thead>\n<tbody>\n");
        while (rs.next()) {
            if (njob % 2 == 0) sb.append("<tr class='evenrow'>");
            else sb.append("<tr class='oddrow'>");
            sb.append("<td>").append(rs.getInt("job_id")).append("</td>");
            sb.append("<td>").append(rs.getString("plate_name")).append("</td>");
            sb.append("<td>").append(rs.getString("status")).append("</td>");
            sb.append("<td>").append(rs.getString("progress")).append("</td>");
            sb.append("</tr>\n");
            njob++;
        }
        sb.append("</table>\n");

        sb.append("</body>\n</html>");
        if (njob == 0) {
            sb = new StringBuffer();
            sb.append("<html><head><title>PowerCore Job Status</title></head><style type=\"text/css\">#sadface { padding: 75px; font-size: x-large;" +
                    " font-family: sans-serif; text-align: center; " +
                    " font-weight: bold; width: 700px; " +
                    "margin-left: auto; margin-right: auto; }</style><body>");
            sb.append("<div id=\"sadface\"><img src='http://qhts.nih.gov/htsws/sadface.png'><br>" +
                    "No jobs are being run. I'm bored" +
                    "</div>");
            sb.append("</body></html>");
        }
        pst.clearParameters();
        pst.close();
        closeConnection();

        return ok(sb.toString()).as("text/html");
    }

    public static Result listPlatesAndAssays(String plateName, String settingsName) throws SQLException {
        if (plateName == null || settingsName == null)
            return badRequest("Invalid plate name and/or settings name");
        makeConnection();
        PreparedStatement pst = hcsConn.prepareStatement("SELECT assays.assay_id," +
                "  assays.settings_name," +
                "  assay_plates.plate_id, plate_name, " +
                "  to_char(assays.time_created, 'YYYY-MM-DD HH24:MI') as time " +
                " FROM assays, plates, assay_plates" +
                " WHERE plate_name    like ? " +
                " AND assay_plates.plate_id = plates.plate_id" +
                " AND assays.assay_id        = assay_plates.assay_id" +
                " AND assays.settings_name like ?" +
                " AND assay_plates.to_delete = 0 order by time desc");
        pst.setString(1, "%" + plateName + "%");
        pst.setString(2, "%" + settingsName + "%");
        ResultSet resultSet = pst.executeQuery();
        StringBuilder sb = new StringBuilder();
        while (resultSet.next()) {
            for (int i = 1; i <= 4; i++) sb.append(resultSet.getObject(i)).append("\t");
            sb.append(resultSet.getObject(5)).append("\n");
        }
        pst.close();
        closeConnection();
        return ok(sb.toString()).as("text/plain");
    }

    public static Result isDataAvailable(String plateName, String settingsName) throws SQLException {
        makeConnection();
        PreparedStatement pst = hcsConn.prepareStatement("SELECT assays.assay_id," +
                "  assay_name," +
                "  settings_name," +
                "  table_id," +
                "  row_count," +
                "  assay_plates.*" +
                " FROM assays, plates, assay_plates" +
                " WHERE plate_name    like ? " +
                " AND assay_plates.plate_id = plates.plate_id" +
                " AND assays.assay_id        = assay_plates.assay_id" +
                " AND assays.settings_name like ?" +
                " AND assay_plates.to_delete = 0");
        pst.setString(1, "%" + plateName + "%");
        pst.setString(2, "%" + settingsName + "%");
        ResultSet resultSet = pst.executeQuery();

        int nassay = 0;
        String tableName = null;
        while (resultSet.next()) {
            tableName = resultSet.getString("table_id");
            nassay++;
        }
        System.out.println("tableName = " + tableName);
        if (nassay != 1 || tableName == null) {
            System.out.println("isDataAvailable: No assay data for this combination of plate & settings name");
            return notFound("No assay data for this combination of plate & settings name");
        }
        pst.clearParameters();

        // OK, now pull out the table data
        pst = hcsConn.prepareStatement("select * from " + tableName + " where rownum < 2");
        resultSet = pst.executeQuery();
        boolean status = resultSet.next();
        resultSet.close();
        pst.close();
        closeConnection();

        if (!status) return notFound("No data available for " + plateName + "/" + settingsName);
        return ok("true").as("text/plain");
    }
    static class WellImage {
        int row, col;
        Set<String> paths;

        WellImage() {
            paths = new TreeSet<>();
        }

        WellImage(int row, int col, Set<String> paths) {
            this.row = row;
            this.col = col;
            this.paths = paths;
        }

        public boolean equals(Object o) {
            if (!(o instanceof WellImage)) return false;
            WellImage wi = (WellImage) o;
            return row == wi.row && col == wi.col;
        }

        // from http://stackoverflow.com/a/15878758/58681
        public int hashCode() {
            int res = Math.max(row, col);
            res = (res << 16) | (res >>> 16);  // exchange top and bottom 16 bits.
            res = res ^ Math.min(row, col);
            return res;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public Set<String> getPaths() {
            return paths;
        }
    }

}