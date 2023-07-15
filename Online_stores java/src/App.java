import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


class Online_store{
    int logged_user = -1;
    int carted_p_id;
    int carted_quant;
    private String user_password;
    private String user_Name;


    boolean Signup(String Name, String Email, double Phone_Number, String Address, int Pin_code, String Password) {
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password

        // Create a connection
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            // System.out.println("Database connection established!");

            String sql_query_1 = "select count(*) from user where name = ?";
            PreparedStatement statement1_1 = connection.prepareStatement(sql_query_1);
            statement1_1.setString(1, Name);
            ResultSet  resultSet= statement1_1.executeQuery();
            resultSet.next();
            int count = resultSet.getInt("count(*)");
            if(count == 0) {
                String sql_query = "insert into user (Name, Email, Phone_number, address, pin_code, password) values (?,?,?,?,?,?);";
                PreparedStatement statement1 = connection.prepareStatement(sql_query);
                statement1.setString(1, Name);
                statement1.setString(2, Email);
                statement1.setDouble(3, Phone_Number);
                statement1.setString(4, Address);
                statement1.setInt(5, Pin_code);
                statement1.setString(6, Password);
                statement1.executeUpdate();
            } else {
                System.out.println("Name is already present!");
                statement.close();
                connection.close();
                return false;
            }

            // System.out.println("Signed up succes!");
            statement.close();
            connection.close();
            return true;
        } 
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Database connection failed!");
            return false;
        }
    }

    boolean Login(String Email, String Password) {
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password

        // Create a connection
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            // System.out.println("Database connection established!");
            String sql_query = "select count(*) from user where email = ?";

            PreparedStatement statement1 = connection.prepareStatement(sql_query);

            statement1.setString(1, Email);

            ResultSet resultSet = statement1.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if(count == 1) {
                // System.out.println("Email found");
                String sql_query1 = "select count(*) from user where email = ? and password = ?";
                PreparedStatement statement2 = connection.prepareStatement(sql_query1);
                statement2.setString(1, Email);                
                statement2.setString(2, Password);
                ResultSet resultSet1 = statement2.executeQuery();
                resultSet1.next();
                int count1 = resultSet1.getInt(1);
                if (count1 == 1) {
                    String sql_query2 = "select user_id, name from user where email = ?";
                    PreparedStatement statement3 = connection.prepareStatement(sql_query2);
                    statement3.setString(1, Email);                
                    ResultSet resultSet2 = statement3.executeQuery();
                    resultSet2.next();
                    logged_user = resultSet2.getInt("user_id");
                    user_Name = resultSet2.getString("name");
                    this.user_password = Password;
                    // System.out.println("Logged in!");
                    connection.close();
                    return true;
                }
                else {
                    System.out.println("Wrong credentials");
                    connection.close();
                    return false;
                }
            }
            else {
                System.out.println("No user Found with this Email");
                connection.close();
                return false;
            }
        }
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Database connection failed!");
            return false;
        }
    }

    void Logout(){
        logged_user = -1;
        System.out.println("logged out success!");
    }

    boolean Show_product_list() {
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password
        Scanner sc = new Scanner(System.in);
        // Create a connection  
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            // System.out.println("Database connection established!");
            String sql_query = "select * from product;";
            PreparedStatement statement = connection.prepareStatement(sql_query);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("ID  | Product Name              | Description                                        | Price   | Categorie       | Quantity   | Availability | Compant |");
            System.out.println("_________________________________________________________________________________________________________________________________________________________");
            boolean present=false;
            while(resultSet.next()) {
                present = true;
                int Id = resultSet.getInt("product_id");
                String product_Name = resultSet.getString("product_name");
                String Description = resultSet.getString("Description");
                int price = resultSet.getInt("price");
                String Categorie = resultSet.getString("categorie");
                int Quantity = resultSet.getInt("Quantity");
                boolean Availablity = resultSet.getBoolean("availability");
                String Company = resultSet.getString("company");
                System.out.printf("%-3d | %-25s | %-50s | %-7d | %-15s | %-10d | %-12b | %-7s |\n", Id, product_Name, Description, price, Categorie,
                Quantity,Availablity, Company);
            }
            System.out.println("_________________________________________________________________________________________________________________________________________________________\n");

            if(!present) {
                System.out.println("Product is not present at this time!");
            }
            if(present) {
                System.out.print("If you want to buy product. Then enter the ID of product otherwise -1: ");
                int Id = sc.nextInt();
                if(Id != -1) {
                    System.out.print("Enter the quantity of product: ");
                    int Quantitys = sc.nextInt();
                    String sql_query1 = "select price, quantity from product where product_id = ?";
                    PreparedStatement statement1 = connection.prepareStatement(sql_query1);
                    statement1.setInt(1, Id);
                    ResultSet resultSet1 = statement1.executeQuery();
                    resultSet1.next();
                    int price = resultSet1.getInt("price");
                    int Quantity_present = resultSet1.getInt("quantity");
                    if(Quantity_present >= Quantitys) {
                        System.out.println("purchased!");
                        Add_purchase_history(Id, Quantitys, price);
                        Update_product_list(Id, Quantitys);
                    } else {
                        System.out.println("Sorry, Number of product is not available at this time.");
                    }
                }
    
                System.out.print("If you want to add product in Cart. Then enter the ID of product otherwise -1: ");
                int p_id = sc.nextInt();
                if(p_id != -1) {
                    System.out.print("enter the Quantity of product: ");
                    int quant = sc.nextInt();
                    carted_p_id = p_id;
                    carted_quant = quant;
                    Add_to_cart();
                }
            }

            connection.close();
            return true;
        }
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Database connection failed!");
            return false;
        }
    }

    boolean Search_product_by_Name() {
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password

        // Create a connection
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);

            // System.out.println("Database connection established!");
            String sql_query_1 = "select * from product;";
            PreparedStatement statement_1 = connection.prepareStatement(sql_query_1);
            ResultSet resultSet_1 = statement_1.executeQuery();
            System.out.println("ID  | Product Name              | Description                                        | Price   | Categorie       | Quantity   | Availability | Compant |");
            System.out.println("_________________________________________________________________________________________________________________________________________________________");
            boolean presents=false;
            while(resultSet_1.next()) {
                presents = true;
                int Id = resultSet_1.getInt("product_id");
                String product_Name = resultSet_1.getString("product_name");
                String Description = resultSet_1.getString("Description");
                int price = resultSet_1.getInt("price");
                String Categorie = resultSet_1.getString("categorie");
                int Quantity = resultSet_1.getInt("Quantity");
                boolean Availablity = resultSet_1.getBoolean("availability");
                String Company = resultSet_1.getString("company");
                System.out.printf("%-3d | %-25s | %-50s | %-7d | %-15s | %-10d | %-12b | %-7s |\n", Id, product_Name, Description, price, Categorie,
                Quantity,Availablity, Company);
            }
            System.out.println("_________________________________________________________________________________________________________________________________________________________\n");

            if(!presents) {
                System.out.println("Product is not present at this time!");
            } else {
                // System.out.println("Database connection established!");
                Scanner sc = new Scanner(System.in);
                System.out.print("Enter the Exact Name: ");
                String P_name = sc.nextLine();
                P_name = P_name.toLowerCase();
                String sql_query = "select * from product where product_name = ?";
                PreparedStatement statement = connection.prepareStatement(sql_query);
                statement.setString(1, P_name.toString());
                ResultSet resultSet = statement.executeQuery();
                System.out.println("ID  | Product Name              | Description                                        | Price   | Categorie       | Quantity   | Availability | Compant |");
                System.out.println("_________________________________________________________________________________________________________________________________________________________");
                boolean present = false;
                // int Id = 0;
                int price = 0;
                int Quantity = 0;
                while(resultSet.next()) {
                    present = true;
                    int Id = resultSet.getInt("product_id");
                    String product_Name = resultSet.getString("product_name");
                    String Description = resultSet.getString("Description");
                    price = resultSet.getInt("price");
                    String Categorie = resultSet.getString("categorie");
                    Quantity = resultSet.getInt("Quantity");
                    boolean Availablity = resultSet.getBoolean("availability");
                    String Company = resultSet.getString("company");
                    System.out.printf("%-3d | %-25s | %-50s | %-7d | %-15s | %-10d | %-12b | %-7s |\n", Id, product_Name, Description, price, Categorie,
                    Quantity,Availablity, Company);
                }
                System.out.println("_________________________________________________________________________________________________________________________________________________________");
                System.out.println("\n");
                if(!present) {
                    System.out.println("Product is not present at this time!");
                }
    
                System.out.print("If you want to buy product. Then enter product ID else -1: ");
                int Id = sc.nextInt();
                if(Id != -1) {
                    System.out.print("Enter the quantity of product: ");
                    int Quantitys = sc.nextInt();
                    if(Quantitys <= Quantity) {
                        System.out.println("purchased!");
                        Add_purchase_history(Id, Quantitys, price);
                        Update_product_list(Id, Quantitys);
                    } else {
                        System.out.println("Sorry, Number of product is not available at this time.");
                    }
                }
    
                System.out.print("If you want to add product in Cart. Then enter 'Yes' of product otherwise 'No': ");
                String want_or_not = sc.next();
                if((want_or_not.toLowerCase()).equals("yes") && Id != 0) {
                    System.out.print("enter the Quantity of product: ");
                    int quant = sc.nextInt();
                    carted_p_id = Id;
                    carted_quant = quant;
                    Add_to_cart();
                }
            }
            connection.close();
            return true;
        } 
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Database connection failed!");
            return false;
        }
    }

    boolean Search_products_categoriewise() {
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password

        // Create a connection
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);

            // System.out.println("Database connection established!");
            String sql_query_1 = "select * from product;";
            PreparedStatement statement_1 = connection.prepareStatement(sql_query_1);
            ResultSet resultSet_1 = statement_1.executeQuery();
            System.out.println("ID  | Product Name              | Description                                        | Price   | Categorie       | Quantity   | Availability | Compant |");
            System.out.println("_________________________________________________________________________________________________________________________________________________________");
            boolean presents=false;
            while(resultSet_1.next()) {
                presents = true;
                int Id = resultSet_1.getInt("product_id");
                String product_Name = resultSet_1.getString("product_name");
                String Description = resultSet_1.getString("Description");
                int price = resultSet_1.getInt("price");
                String Categorie = resultSet_1.getString("categorie");
                int Quantity = resultSet_1.getInt("Quantity");
                boolean Availablity = resultSet_1.getBoolean("availability");
                String Company = resultSet_1.getString("company");
                System.out.printf("%-3d | %-25s | %-50s | %-7d | %-15s | %-10d | %-12b | %-7s |\n", Id, product_Name, Description, price, Categorie,
                Quantity,Availablity, Company);
            }
            System.out.println("_________________________________________________________________________________________________________________________________________________________\n");

            if(!presents) {
                System.out.println("Product is not present at this time!");
            } else {
                // System.out.println("Database connection established!");
                Scanner sc = new Scanner(System.in);
                System.out.print("Enter the Exact Categories Name: ");
                String P_name = sc.nextLine();
                P_name = P_name.toLowerCase();
                String sql_query = "select * from product where Categorie = ?";
                PreparedStatement statement = connection.prepareStatement(sql_query);
                statement.setString(1, P_name.toString());
                ResultSet resultSet = statement.executeQuery();
                System.out.println("ID  | Product Name              | Description                                        | Price   | Categorie       | Quantity   | Availability | Compant |");
                System.out.println("_________________________________________________________________________________________________________________________________________________________");
                boolean present = false;
                while(resultSet.next()) {
                    present = true;
                    int Id = resultSet.getInt("product_id");
                    String product_Name = resultSet.getString("product_name");
                    String Description = resultSet.getString("Description");
                    int price = resultSet.getInt("price");
                    String Categorie = resultSet.getString("categorie");
                    int Quantity = resultSet.getInt("Quantity");
                    boolean Availablity = resultSet.getBoolean("availability");
                    String Company = resultSet.getString("company");
                    System.out.printf("%-3d | %-25s | %-50s | %-7d | %-15s | %-10d | %-12b | %-7s |\n", Id, product_Name, Description, price, Categorie,
                    Quantity,Availablity, Company);
                }
                System.out.println("_________________________________________________________________________________________________________________________________________________________");
                System.out.println("\n");
                if(!present) {
                    System.out.println("Product is not present at this time!");
                }
                if(present) {
                    System.out.print("If you want to buy product. Then enter the ID of product otherwise -1: ");
                    int Id = sc.nextInt();
                    if(Id != -1) {
                        System.out.print("Enter the quantity of product: ");
                        int Quantitys = sc.nextInt();
                        String sql_query1 = "select price, quantity from product where product_id = ?";
                        PreparedStatement statement1 = connection.prepareStatement(sql_query1);
                        statement1.setInt(1, Id);
                        ResultSet resultSet1 = statement1.executeQuery();
                        resultSet1.next();
                        int price = resultSet1.getInt("price");
                        int Quantity_present = resultSet1.getInt("quantity");
                        if(Quantity_present >= Quantitys) {
                            System.out.println("purchased!");
                            Add_purchase_history(Id, Quantitys, price);
                            Update_product_list(Id, Quantitys);
                        } else {
                            System.out.println("Sorry, Number of product is not available at this time.");
                        }
                    }
        
                    System.out.print("If you want to add product in Cart. Then enter the ID of product otherwise -1: ");
                    int p_id = sc.nextInt();
                    if(p_id != -1) {
                        System.out.print("enter the Quantity of product: ");
                        int quant = sc.nextInt();
                        carted_p_id = p_id;
                        carted_quant = quant;
                        Add_to_cart();
                    }
                }
            }
            connection.close();
            return true;
        }
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Database connection failed!");
            return false;
        }
    }

    boolean Show_cart() {
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password

        // Create a connection
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            // System.out.println("Database connection established!");
            String sql_query = "select c.cart_id,p.product_id,p.product_name,p.Description,p.price,p.categorie,c.Quantity,p.availability,p.company from user as u Inner join cart as c on c.user_id = u.user_id and u.user_id = ? inner join product as p on p.product_id = c.product_id;";
            PreparedStatement statement = connection.prepareStatement(sql_query);
            statement.setInt(1, logged_user);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("ID  | Product Name              | Description                                        | Price   | Categorie       | Quantity   | Availability | Company |");
            System.out.println("_________________________________________________________________________________________________________________________________________________________");
            boolean present = false;
            while(resultSet.next()) {
                present = true;
                int Id = resultSet.getInt("p.product_id");
                String product_Name = resultSet.getString("p.product_name");
                String Description = resultSet.getString("p.Description");
                int price = resultSet.getInt("p.price");
                String Categorie = resultSet.getString("p.categorie");
                int Quantity = resultSet.getInt("c.Quantity");
                boolean Availablity = resultSet.getBoolean("p.availability");
                String Company = resultSet.getString("p.company");
                System.out.printf("%-3d | %-25s | %-50s | %-7d | %-15s | %-10d | %-12b | %-7s |\n", Id, product_Name, Description, price, Categorie,
                Quantity,Availablity, Company);
            }
            System.out.println("_________________________________________________________________________________________________________________________________________________________");
            System.out.println("\n");
            if(!present) {
                System.out.println("Product is not present at this time!");
            }
            Scanner sc = new Scanner(System.in);
            if(present) {
                System.out.print("If you want to buy product. Then enter the ID of product otherwise -1: ");
                int Id = sc.nextInt();
                if(Id != -1) {
                    System.out.print("Enter the quantity of product: ");
                    int Quantitys = sc.nextInt();
                    String sql_query1 = "select price, quantity from product where product_id = ?";
                    PreparedStatement statement1 = connection.prepareStatement(sql_query1);
                    statement1.setInt(1, Id);
                    ResultSet resultSet1 = statement1.executeQuery();
                    resultSet1.next();
                    int price = resultSet1.getInt("price");
                    int quantity_present = resultSet1.getInt("quantity");
                    if(quantity_present >= Quantitys) {
                        String sql_query2 = "delete from cart where product_id=? and quantity=?;";
                        PreparedStatement statement2 = connection.prepareStatement(sql_query2);
                        statement2.setInt(1, Id);
                        statement2.setInt(2, Quantitys);
                        statement2.executeUpdate();
                        System.out.println("purchased!");
                        Add_purchase_history(Id, Quantitys, price);
                        Update_product_list(Id, Quantitys);
                        
                    } else {
                        System.out.println("Sorry, Number of product is not available at this time.");
                    }
                }
            }
            connection.close();
            return true;
        } 
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Database connection failed!");
            return false;
        }
    }
    
    boolean Add_to_cart() {
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password

        // Create a connection
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            // System.out.println("Database connection established!");
            String sql_query = "insert into cart (user_id, product_id, price, date_time, quantity) values (?,?,?,?,?);";
            PreparedStatement statement = connection.prepareStatement(sql_query);
            statement.setInt(1, logged_user);
            statement.setInt(2, carted_p_id);
            statement.setInt(3, 0);
            statement.setString(4, Date_time());
            statement.setInt(5, carted_quant);
            statement.executeUpdate();
            carted_p_id = -1;
            carted_quant = -1;
            System.out.println("Add success in cart!");
            connection.close();
            return true;
        } 
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Database connection failed!");
            return false;
        }
    }

    boolean Delete_from_cart() {
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password

        // Create a connection
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            // System.out.println("Database connection established!");
            String sql_query = "select c.cart_id,p.product_id,p.product_name,p.Description,p.price,p.categorie,c.Quantity,p.availability,p.company from user as u Inner join cart as c on c.user_id = u.user_id and u.user_id = ? inner join product as p on p.product_id = c.product_id;";
            PreparedStatement statement = connection.prepareStatement(sql_query);
            statement.setInt(1, logged_user);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("ID  | Product Name              | Description                                        | Price   | Categorie       | Quantity   | Availability | Company |");
            System.out.println("_________________________________________________________________________________________________________________________________________________________");
            boolean present = false;
            while(resultSet.next()) {
                present = true;
                int Id = resultSet.getInt("c.cart_id");
                String product_Name = resultSet.getString("p.product_name");
                String Description = resultSet.getString("p.Description");
                int price = resultSet.getInt("p.price");
                String Categorie = resultSet.getString("p.categorie");
                int Quantity = resultSet.getInt("c.Quantity");
                boolean Availablity = resultSet.getBoolean("p.availability");
                String Company = resultSet.getString("p.company");
                System.out.printf("%-3d | %-25s | %-50s | %-7d | %-15s | %-10d | %-12b | %-7s |\n", Id, product_Name, Description, price, Categorie,
                Quantity,Availablity, Company);
            }
            System.out.println("_________________________________________________________________________________________________________________________________________________________");
            System.out.println("\n");
            if(!present) {
                System.out.println("Product is not present at this time!");
            }

            System.out.print("Enter cart id in case of delete otherwise -1: ");
            Scanner sc = new Scanner(System.in);
            int id = sc.nextInt();
            
            // System.out.println("Delete_from_cart - Database connection established!");
            String sql_query1 = "delete from cart where cart_id = ?";
            PreparedStatement statement1 = connection.prepareStatement(sql_query1);
            statement1.setInt(1, id);

            statement1.executeUpdate();
            
            connection.close();
            return true;
        } 
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Delete_from_cart - Database connection failed!");
            return false;
        }
    }

    boolean Add_purchase_history(int Id, int Quantity, int price) {
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password

        // Create a connection
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            // System.out.println("Add_purchase_history - Database connection established!");
            String sql_query = "insert into purchase_history (user_id, product_id, price, date_time, quantity) values(?,?,?,?,?);";
            PreparedStatement statement = connection.prepareStatement(sql_query);
            statement.setInt(1, logged_user);
            statement.setInt(2,Id);
            statement.setInt(3, price);
            statement.setString(4, Date_time());
            statement.setInt(5, Quantity);
            statement.executeUpdate();
            connection.close();
            return true;
        } 
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Add_purchase_history - Database connection failed!");
            return false;
        }
    }

    boolean Show_purchase_history(){
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password

        // Create a connection
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            // System.out.println("Show_purchase_history - Database connection established!");
            String sql_query = "select pr.product_history_id,p.product_name,p.Description,p.price,p.categorie,pr.Quantity,pr.date_time,p.company from user as u Inner join purchase_history as pr on pr.user_id = u.user_id and u.user_id = ? inner join product as p on p.product_id = pr.product_id;";
            PreparedStatement statement = connection.prepareStatement(sql_query);
            statement.setInt(1, logged_user);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("ID  | Product Name              | Description                                        | Price   | Categorie       | Quantity   | DateTime            | Company |");
            System.out.println("______________________________________________________________________________________________________________________________________________________________");
            boolean present = false;
            while(resultSet.next()) {
                present = true;
                int Id = resultSet.getInt("pr.product_history_id");
                String product_Name = resultSet.getString("p.product_name");
                String Description = resultSet.getString("p.Description");
                int price = resultSet.getInt("p.price");
                String Categorie = resultSet.getString("p.categorie");
                int Quantity = resultSet.getInt("pr.Quantity");
                String DT = resultSet.getString("pr.date_time");
                String Company = resultSet.getString("p.company");
                System.out.printf("%-3d | %-25s | %-50s | %-7d | %-15s | %-10d | %-18s | %-7s |\n", Id, product_Name, Description, price, Categorie,
                Quantity,DT, Company);
            }
            System.out.println("_______________________________________________________________________________________________________________________________________________________________");
            System.out.println("\n");
            if(!present) {
                System.out.println("Product is not present at this time!");
            }
            System.out.print("If you want to reset purchase history enter 'yes' else 'no': ");
            Scanner sc  = new Scanner(System.in);
            String reset = sc.next();
            if((reset.toLowerCase()).equals("yes")) {
                System.out.print("Enter password: ");
                String pass = sc.next();
                if(user_password.equals(pass)) {
                    String sql_query1 = "delete from purchase_history;";
                    PreparedStatement statement1 = connection.prepareStatement(sql_query1);
                    statement1.executeUpdate();
                    System.out.println("Sucessfully Reset History!");
                } else {
                    System.out.println("Wrong password! Try Again!");
                }
            }
            connection.close();
            return true;
        } 
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Show_purchase_history - Database connection failed!");
            return false;
        }
    }

    boolean Update_product_list(int P_id, int Quant) {
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password

        // Create a connection
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            // System.out.println("Update_product_list - Database connection established!");
            String sql_query = "update product set quantity = quantity - ? where product_id = ?;";
            PreparedStatement statement = connection.prepareStatement(sql_query);
            statement.setInt(1, Quant);
            statement.setInt(2, P_id);
            statement.executeUpdate();

            String sql_query1 = "select quantity from product where product_id=?";
            PreparedStatement statement1 = connection.prepareStatement(sql_query1);
            statement1.setInt(1, P_id);
            ResultSet resultSet= statement1.executeQuery();
            resultSet.next();
            int quantity = resultSet.getInt("quantity");

            if(quantity == 0) {
                String sql_query2 = "update product set availability = 0 where product_id = ?;";
                PreparedStatement statement2 = connection.prepareStatement(sql_query2);
                statement2.setInt(1, P_id);
                statement2.executeUpdate();
            }

            connection.close();
            return true;
        } 
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Update_product_list - Database connection failed!");
            return false;
        }
    }

    String Date_time() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    boolean Update_account(String U_Name, String U_Email, double U_phone_number, String U_Address, int U_Pin_code, String U_Password) {
        String url = "jdbc:mysql://localhost:3306/online_store"; // Replace "mydatabase" with your database name
        String username = "root"; // Replace with your MySQL username
        String password = "12345"; // Replace with your MySQL password

        // Create a connection
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            // System.out.println("Update_product_list - Database connection established!");

            String sql_query1 = "select * from user where name=?";
            PreparedStatement statement1 = connection.prepareStatement(sql_query1);
            statement1.setString(1, user_Name);
            ResultSet resultSet= statement1.executeQuery();
            resultSet.next();
            
            String sql_query = "update user set name = ?, email = ?, phone_number = ?, address = ?, pin_code = ?, password = ? where name = ?;";
            PreparedStatement statement = connection.prepareStatement(sql_query);
            statement.setString(1,U_Name);
            statement.setString(2, U_Email);
            statement.setDouble(3, U_phone_number);
            statement.setString(4, U_Address);
            statement.setInt(5, U_Pin_code);
            statement.setString(6, U_Password);
            statement.setString(7,user_Name);
            statement.executeUpdate();
            System.out.println("Succesfully update done");
            connection.close();
            return true;
        } 
        catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Update_product_list - Database connection failed!");
            return false;
        }
    }
}


public class App {
    static void flush_kar_do() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Want to open Online_stor (yes/no): ");
        String want_open = sc.next();
        if((want_open.toLowerCase()).equals("yes")) {
            Online_store online_store = new Online_store();
            while(true) {
                flush_kar_do();
                System.out.println("1. Sign up\n2. Login\n3. Exit");
                System.out.print("Enter choice here: ");
                int choice1 = sc.nextInt();
                if(choice1 == 1) {
                    try{
                        System.out.print("Enter Name: ");
                        sc.nextLine();
                        String Name = sc.nextLine();
                        System.out.print("Enter Email: ");
                        String Email = sc.nextLine();
                        System.out.print("Enter phone_number: ");
                        double phone_number = sc.nextDouble();
                        System.out.print("Enter Address: ");
                        sc.nextLine();
                        String Address = sc.nextLine();
                        System.out.print("Enter Pin_code: ");
                        int Pin_code = sc.nextInt();
                        System.out.print("Enter Password: ");
                        sc.nextLine();
                        String Password = sc.nextLine();
                        boolean sign = online_store.Signup(Name, Email, phone_number, Address, Pin_code, Password);
                        if(sign) {
                            System.out.println("Sucessfully Signed up. Now you can login");
                        }
                        Thread.sleep(3000);
                    } catch(Exception e) {
                        System.out.println("something is wrong here!");
                    }
                } else if(choice1 == 2) {
                    try{
                        System.out.print("Enter Email: ");
                        sc.nextLine();
                        String Email = sc.nextLine();
                        System.out.print("Enter Password: ");
                        String Password = sc.nextLine();
                        boolean login = online_store.Login(Email, Password);
                        if (login) {
                            System.out.println("Succesfully, Login in!");
                            Thread.sleep(2000);
                            while(true) {
                                Thread.sleep(2000);
                                flush_kar_do();
                                System.out.println("\n1. See Products\n2. Search Product by Name\n3. Search Product by Categorie\n4. Show Cart\n5. Delete From Cart\n6. See Purchase History\n7. Update your Account\n8. Exit");
                                System.out.print("Enter your choice here: ");
                                int ops = sc.nextInt();
                                if(ops == 1) {
                                    online_store.Show_product_list();
                                } else if(ops == 2) {
                                    online_store.Search_product_by_Name();
                                } else if(ops == 3) {
                                    online_store.Search_products_categoriewise();
                                } else if(ops == 4) {
                                    online_store.Show_cart();
                                } else if(ops == 5) {
                                    online_store.Delete_from_cart();
                                } else if(ops == 6) {
                                    online_store.Show_purchase_history();
                                } else if(ops == 7) {
                                    System.out.print("Enter Name: ");
                                    sc.nextLine();
                                    String U_Name = sc.nextLine();
                                    System.out.print("Enter Email: ");
                                    String U_Email = sc.nextLine();
                                    System.out.print("Enter phone_number: ");
                                    double U_phone_number = sc.nextDouble();
                                    System.out.print("Enter Address: ");
                                    sc.nextLine();
                                    String U_Address = sc.nextLine();
                                    System.out.print("Enter Pin_code: ");
                                    int U_Pin_code = sc.nextInt();
                                    System.out.print("Enter Password: ");
                                    sc.nextLine();
                                    String U_Password = sc.nextLine();
                                    boolean us = online_store.Update_account(U_Name, U_Email, U_phone_number, U_Address, U_Pin_code, U_Password);
                                    if(!us) {
                                        System.out.println("Something is wrong");
                                    }
                                } else if(ops == 8) {
                                    System.out.println("Logged out");
                                    online_store.Logout();
                                    break;
                                } else {
                                    System.out.println("Invalid choice!");
                                }
                            }
                        }
                    } catch(Exception e) {
                        System.out.println("something is wrong here!");
                    }
                    
                } else {
                    System.out.println("Exited");
                    break;
                }
            }
        } else {
            System.out.println("Exited");
        }
        
    }
}

// sameer7417277576@gmail.com
// 12345678