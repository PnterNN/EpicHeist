package mc.pnternn.epicheist.sql;

import mc.pnternn.epicheist.managers.Crew;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQL {
    private Connection connection;
    public MySQL(String host, String port, String database, String username, String password){
        synchronized (this) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Could not connect to MySQL database: " + e.getMessage());
            }
        }
    }
    public void createTable()  {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS crews (id VARCHAR(255), name VARCHAR(255), leader VARCHAR(255), members TEXT)");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not create table: " + e.getMessage());
        }
    }

    public void createCrew(String id, String name, String leader) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO crews (id, name,leader) VALUES ('" + id + "', '" + name + "', '" + leader + "')");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not create crew: " + e.getMessage());
        }
    }
    public void deleteCrew(String id) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM crews WHERE id='" + id + "'");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not delete crew: " + e.getMessage());
        }
    }
    public void setLeader(String id, String leader) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE crews SET leader='" + leader + "' WHERE id='" + id + "'");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not set leader: " + e.getMessage());
        }
    }
    public void addMember(String id, String member) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE crews SET members=COALESCE(' ') WHERE id='" + id + "'");
            statement.executeUpdate("UPDATE crews SET members=CONCAT(members, '" + member + ",') WHERE id='" + id + "'");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not add member: " + e.getMessage());
        }
    }
    public void removeMember(String id, String member) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE crews SET members = TRIM(members) WHERE id = '"+id+"';");
            statement.executeUpdate("UPDATE crews SET members=REPLACE(members, '"+member+",','') WHERE id= '"+id+"'");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not remove member: " + e.getMessage());
        }
    }
    public void renameCrew(String id, String name) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE crews SET name='" + name + "' WHERE id='" + id + "'");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not rename crew: " + e.getMessage());
        }
    }
    public List<Crew> getCrews(){
        List<Crew> crews = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM crews");
            while (resultSet.next()) {
                List<OfflinePlayer> members = new ArrayList<>();
                if(resultSet.getString("members") != null){
                    for (String member : resultSet.getString("members").replace(" ","").split(",")) {
                        if(member.length() > 3){
                            members.add(Bukkit.getOfflinePlayer(UUID.fromString(member)));
                        }
                    }
                }
                crews.add(new Crew(UUID.fromString(resultSet.getString("id")), Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("leader"))),resultSet.getString("name"), members));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not load crews: " + e.getMessage());
        }
        return crews;
    }
}
