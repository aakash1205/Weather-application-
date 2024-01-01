import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;

public class WeatherApp2 extends JFrame implements ActionListener {

    private static final String API_KEY = "38f6759f3597c9c875273068901278df";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    private JPanel panel;
    private JTextField textField;
    private JButton button;
    private JLabel label;

    private List<String> availableCities;

    public WeatherApp2() {
        setTitle("Weather App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        availableCities = new ArrayList<>();

        // Create a JPanel with a BorderLayout
        panel = new JPanel(new BorderLayout()) {
            // @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image image = new ImageIcon("C:/Users/K AAKASH/Downloads/imgg.jpg").getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Add the heading label to the top of the panel
        JLabel headingLabel = new JLabel("Welcome to Weather Finder");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headingLabel.setForeground(Color.WHITE);
        headingLabel.setHorizontalAlignment(JLabel.CENTER);
        headingLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(headingLabel, BorderLayout.NORTH);

        // Create a center panel with a GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 10, 0, 0);

        // Add the label and text field for the location
        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setFont(new Font("Arial", Font.BOLD, 24));
        locationLabel.setForeground(Color.WHITE);
        centerPanel.add(locationLabel, c);

        c.gridx++;
        textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.addActionListener(this);
        centerPanel.add(textField, c);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Add the label for displaying weather information
        label = new JLabel();
        label.setFont(new Font("Arial", Font.BOLD, 46));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label, BorderLayout.SOUTH);

        // Create a buttons panel and add it to the right side of the panel
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.PLAIN, 12));
        startButton.addActionListener(this);
        buttonsPanel.add(startButton, gbc);

        gbc.gridy++;
        JButton helpButton = new JButton("Help");
        helpButton.setFont(new Font("Arial", Font.PLAIN, 12));
        helpButton.addActionListener(this);
        buttonsPanel.add(helpButton, gbc);

        gbc.gridy++;
        JButton aboutButton = new JButton("About Us");
        aboutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        aboutButton.addActionListener(this);
        buttonsPanel.add(aboutButton, gbc);

        gbc.gridy++;
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 12));
        exitButton.addActionListener(this);
        buttonsPanel.add(exitButton, gbc);

        panel.add(buttonsPanel, BorderLayout.EAST);

        add(panel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Start":
                String location = textField.getText();

                try {
                    URL url = new URL(API_URL + "?q=" + location + "&appid=" + API_KEY);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");

                    if (conn.getResponseCode() != 200) {
                        showError("Unable to retrieve weather data");
                        return;
                    }

                    String data = readResponse(conn.getInputStream());
                    double tempKelvin = getTemperature(data);
                    double tempCelsius = tempKelvin - 273.15;

                    label.setText(String.format("Temperature in %s is %.2f Celsius.", location, tempCelsius));
                } catch (IOException ex) {
                    showError("Unable to connect to weather server");
                }

                break;
            case "Help":
                JOptionPane.showMessageDialog(this,
                        "1. Click on the start.\n2. Enter the name of a city and click the 'Search' button to find the current weather for that city.\n3.click on about us to know about ourselves.\n4. Click on exit to exit the screen.\n");
                break;
            case "About Us":
                JOptionPane.showMessageDialog(this,
                        "Weather Finder is a Java application developed by Sharan and his gang..!.\n1. Sharan a man with integrity\n2. Aakash a man with prosperity\n3. Mohith a man with diversity\n4. Umar a man with generosity\n");
                break;
            case "Exit":
                System.exit(0);
                break;
            default:
                // If the action is not one of the buttons, check if it's triggered by the text
                // field
                if (e.getSource() == textField) {
                    String prefix = textField.getText().toLowerCase();
                    List<String> matchingCities = new ArrayList<>();

                    for (String city : availableCities) {
                        if (city.toLowerCase().startsWith(prefix)) {
                            matchingCities.add(city);
                        }
                    }

                    if (!matchingCities.isEmpty()) {
                        String[] options = matchingCities.toArray(new String[0]);
                        String selectedCity = (String) JOptionPane.showInputDialog(this,
                                "Select a city:",
                                "Available Cities",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                options,
                                null);

                        if (selectedCity != null) {
                            textField.setText(selectedCity);
                        }
                    }
                }
                break;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String readResponse(InputStream input) {
        try (Scanner scanner = new Scanner(input)) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    private double getTemperature(String data) {
        int startIndex = data.indexOf("\"temp\":") + 7;
        int endIndex = data.indexOf(",", startIndex);
        String tempString = data.substring(startIndex, endIndex);
        return Double.parseDouble(tempString);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherApp2 weatherApp = new WeatherApp2();
            weatherApp.loadAvailableCities();
        });
    }

    private void loadAvailableCities() {
        // Simulating the loading of available cities
        availableCities.add("London");
        availableCities.add("New York");
        availableCities.add("Paris");
        availableCities.add("Tokyo");
        availableCities.add("Sydney");
        availableCities.add("Berlin");
        availableCities.add("Rome");
        availableCities.add("Cairo");
        availableCities.add("Moscow");
        availableCities.add("Hyderabad");
        availableCities.add("Vijayawada");
        availableCities.add("Chennai");
        availableCities.add("Guntur");
        availableCities.add("Mangalagiri");
        availableCities.add("Boston");
        availableCities.add("Rio De Janiro");
        availableCities.add("California");
        availableCities.add("Mexico");
        availableCities.add("Ahmedabad");
        availableCities.add("Alexandria");
        availableCities.add("Agra");
        availableCities.add("Amritsar");
        availableCities.add("Allahabad");
        availableCities.add("Amsterdam");
        // b
        availableCities.add("Bangkok");
        availableCities.add("Busan");
        availableCities.add("Bhopal");
        availableCities.add("Bhagalpur");
        availableCities.add("Bellary");
        // c
        availableCities.add("Chennai");
        availableCities.add("Chicago");
        availableCities.add("Coimbatore");
        availableCities.add("Columbus");
        availableCities.add("Chitoor");
        // d
        availableCities.add("Delhi");
        availableCities.add("Dhaka");
        availableCities.add("Dongguan");
        availableCities.add("Dubai");

        // e
        availableCities.add("East godavari");
        availableCities.add("Edur");
        availableCities.add("Ellora");
        availableCities.add("Eluru");

        // f
        availableCities.add("Fazilka");
        availableCities.add("Ferozepur");
        availableCities.add("Falna");
        // g
        availableCities.add("Gadag");
        availableCities.add("Galiakot");
        availableCities.add("Ganapavaram");
        availableCities.add("Gangapur");
        // h
        availableCities.add("Hajipur");
        availableCities.add("Hamirpur ");
        availableCities.add("Hampi");
        availableCities.add("Hanuman Junction");
        // i
        availableCities.add("Indore");
        availableCities.add("Indravati");
        availableCities.add("Itanagar");
        // j
        availableCities.add("jaipur");
        availableCities.add("jodhpur");
        availableCities.add("jhunjhunu");
        availableCities.add("jabalpur");
        // k
        availableCities.add("Kota");
        availableCities.add("Kolkata");
        availableCities.add("Kozhikode");
        availableCities.add("Kollam");
        availableCities.add("Kurnool");

        // l
        availableCities.add("Latur");
        availableCities.add("laguna");
        availableCities.add("laurel");
        availableCities.add("london");
        // m
        availableCities.add("Machilipatnam");
        availableCities.add("Mangalore");
        availableCities.add("Mumbai");
        // n
        availableCities.add("Nagapattinam");
        availableCities.add("Nippani");
        availableCities.add("Nagpur");
        // o
        availableCities.add("Ooty");
        availableCities.add("Ongole");
        availableCities.add("Oman");
        // p
        availableCities.add("Pune");
        availableCities.add("Patna");
        availableCities.add("Palakkad ");
        // q
        availableCities.add("Qingdao");
        availableCities.add("Quthbullapur");
        availableCities.add("Quepem ");
        // r
        availableCities.add("Rajahmundry");
        availableCities.add("Rameswaram");
        availableCities.add("Rajapura ");
        // s
        availableCities.add("Solapur ");
        availableCities.add("Surat ");
        availableCities.add("Salem ");
        // t
        availableCities.add("Thanjavur");
        availableCities.add("Taiwan");
        availableCities.add("Thailand");
        // u
        availableCities.add("Ujjain");
        availableCities.add("Upaplavya");
        availableCities.add("Uthiyur");
        // v
        availableCities.add("Varanasi");
        availableCities.add("Vanchi ");
        availableCities.add("Vrindavan");
        // w
        availableCities.add("Warangal");
        availableCities.add("Wayanad");
        availableCities.add("Wokha");
        // x
        availableCities.add("Xeldem");
        availableCities.add("Xelvona");
        availableCities.add("Xapuri");
        // y
        availableCities.add("Yavatmal");
        availableCities.add("Yelagiri");
        availableCities.add("Yellapur");
        // z
        availableCities.add("Zindānpur");
        availableCities.add("Zerakpur");
        availableCities.add("Zulfgarh");

    }
}
