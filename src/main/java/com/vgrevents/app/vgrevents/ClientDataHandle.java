package com.vgrevents.app.vgrevents;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientDataHandle extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger enquiryCounter = new AtomicInteger(1);

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Generate a sequential enquiry code
        int enquiryCode = enquiryCounter.getAndIncrement();

        // Retrieve form data
        String sourceCountry = request.getParameter("source-country");
        String sourceState = request.getParameter("source-state");
        String sourceCity = request.getParameter("source-city");
        String destinationCountry = request.getParameter("destination-country");
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String checkin = request.getParameter("checkin");
        String checkout = request.getParameter("checkout");
        String bookingType = request.getParameter("bookingType");
        String accommodationType = request.getParameter("accommodationType");
        String hotelCategory = request.getParameter("hotelCategory");

        // Retrieve these as integers
        int adults = parseInt(request.getParameter("adults"));
        int children = parseInt(request.getParameter("children"));
        int numberOfStudents = parseInt(request.getParameter("numberOfStudents"));
        int numberOfCoordinators = parseInt(request.getParameter("numberOfCoordinators"));
        int numberOfRooms = parseInt(request.getParameter("numberOfRooms"));
        int numberOfAttendees = parseInt(request.getParameter("numberOfAttendees"));

        String transportationRequired = request.getParameter("transportationRequired");
        String vehicleType = request.getParameter("vehicleType");
        String specialRequests = request.getParameter("specialRequests");
        String corporateName = request.getParameter("corporateName");
        String schoolName = request.getParameter("schoolName");

        // Get selected destination states and cities from the form submission
        String[] destinationStates = request.getParameterValues("destination-state[]");
        String[] destinationCities = request.getParameterValues("destination-city[]");

        // Format the check-in and check-out dates
        String formattedCheckin = formatDate(checkin);
        String formattedCheckout = formatDate(checkout);

        // Your email address where you want to receive notifications
        String to = "bngowtham1988@gmail.com"; // Your email address

        // Send email to admin
        boolean emailSentToAdmin = sendEmail(to, "Dear Administrator,", enquiryCode, name, phone, email,
                formattedCheckin, formattedCheckout, bookingType, accommodationType, hotelCategory, adults, children,
                numberOfStudents, numberOfCoordinators, numberOfRooms, numberOfAttendees, destinationStates,
                destinationCities, sourceCountry, sourceState, sourceCity, destinationCountry, transportationRequired,
                vehicleType, specialRequests, schoolName, corporateName, true);

        // Send email to client
        boolean emailSentToClient = sendEmail(email, "Dear " + name + ",", enquiryCode, name, phone, email,
                formattedCheckin, formattedCheckout, bookingType, accommodationType, hotelCategory, adults, children,
                numberOfStudents, numberOfCoordinators, numberOfRooms, numberOfAttendees, destinationStates,
                destinationCities, sourceCountry, sourceState, sourceCity, destinationCountry, transportationRequired,
                vehicleType, specialRequests, schoolName, corporateName, false);

        // Inform user about email status
        if (emailSentToAdmin && emailSentToClient) {
            out.println(
                    "<p>Your booking details have been successfully sent to the administrator and a confirmation email has been sent to you.</p>");
        } else {
            out.println("<p>There was an error sending your booking details. Please try again later.</p>");
        }

        // Close the PrintWriter
        out.close();

        // Redirect to the welcome page
        response.sendRedirect("index.html");
    }

    private boolean sendEmail(String to, String greeting, int enquiryCode, String name, String phone, String email,
            String checkin, String checkout, String bookingType, String accommodationType, String hotelCategory,
            int adults, int children, int numberOfStudents, int numberOfCoordinators, int numberOfRooms,
            int numberOfAttendees, String[] destinationStates, String[] destinationCities, String sourceCountry,
            String sourceState, String sourceCity, String destinationCountry, String transportationRequired,
            String vehicleType, String specialRequests, String schoolName, String corporateName,
            boolean includeClientDetails) {

        // Generic SMTP sender's email
        String from = "gowthambn1988@gmail.com"; // Replace with your SMTP email address

        // SMTP server information
        String host = "smtp.gmail.com"; // Change this to your SMTP server
        String username = "gowthambn1988@gmail.com"; // Change this to your SMTP user
        String password = "123byG@1988"; // Use your actual SMTP password

        // Setup properties for the session
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587"); // Use 587 for TLS

        // Get the Session object
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a default MimeMessage object
            Message message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(from));

            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("Booking Inquiry Details");

            // Create the email body content using HTML
            StringBuilder bodyBuilder = new StringBuilder();
            bodyBuilder.append("<html><head><style>");
            // Add some CSS for styling
            bodyBuilder.append(
                    "body { background-color: #e0f7fa; font-family: Arial, sans-serif; color: #333; padding: 20px; }");
            bodyBuilder.append("h4 { color: white; margin: 0; } ");
            bodyBuilder.append("table { width: 100%; border-collapse: collapse; }");
            bodyBuilder.append("th { background-color: #0277bd; color: white; padding: 10px; }");
            bodyBuilder.append("td { padding: 10px; }");
            bodyBuilder.append("</style></head><body>");
            bodyBuilder.append("<p>").append(greeting).append("</p>");
            bodyBuilder.append("<p><strong>Please find below the details of the enquiry:</strong></p>");

            bodyBuilder.append("<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\">");

            if (includeClientDetails) {
                bodyBuilder.append("<tr><th colspan=\"2\"><h4>Client Details</h4></th></tr>");
                bodyBuilder.append("<tr><td><strong>Enquiry Code</strong></td><td>").append(enquiryCode)
                        .append("</td></tr>");
                bodyBuilder.append("<tr><td><strong>Name</strong></td><td>").append(name).append("</td></tr>");
                bodyBuilder.append("<tr><td><strong>Phone</strong></td><td>").append(phone).append("</td></tr>");
                bodyBuilder.append("<tr><td><strong>Email</strong></td><td><i>").append(email).append("</i></td></tr>");
            }

            // Booking Details
            bodyBuilder.append("<tr><th colspan=\"2\"><h4>Booking Details</h4></th></tr>");
            bodyBuilder.append("<tr><td><strong>Booking Type</strong></td><td>").append(bookingType)
                    .append("</td></tr>");
            bodyBuilder.append("<tr><td><strong>Accommodation Type</strong></td><td>").append(accommodationType)
                    .append("</td></tr>");
            bodyBuilder.append("<tr><td><strong>Hotel Category</strong></td><td>").append(hotelCategory)
                    .append("</td></tr>");
            bodyBuilder.append("<tr><td><strong>Check-in Date</strong></td><td>").append(checkin).append("</td></tr>");
            bodyBuilder.append("<tr><td><strong>Check-out Date</strong></td><td>").append(checkout)
                    .append("</td></tr>");

            // Guest Details
            bodyBuilder.append("<tr><th colspan=\"2\"><h4>Guests</h4></th></tr>");
            if (!schoolName.isEmpty()) {
                bodyBuilder.append("<tr><td><strong>School Name</strong></td><td>").append(schoolName)
                        .append("</td></tr>");
            }
            if (!corporateName.isEmpty()) {
                bodyBuilder.append("<tr><td><strong>Corporate Name</strong></td><td>").append(corporateName)
                        .append("</td></tr>");
            }
            if (adults > 0) {
                bodyBuilder.append("<tr><td><strong>Number of Adults</strong></td><td>").append(adults)
                        .append("</td></tr>");
            }
            if (children > 0) {
                bodyBuilder.append("<tr><td><strong>Number of Children</strong></td><td>").append(children)
                        .append("</td></tr>");
            }
            if (numberOfStudents > 0) {
                bodyBuilder.append("<tr><td><strong>Number of Students</strong></td><td>").append(numberOfStudents)
                        .append("</td></tr>");
            }
            if (numberOfCoordinators > 0) {
                bodyBuilder.append("<tr><td><strong>Coordinators</strong></td><td>").append(numberOfCoordinators)
                        .append("</td></tr>");
            }
            if (numberOfAttendees > 0) {
                bodyBuilder.append("<tr><td><strong>Number of Attendies</strong></td><td>").append(numberOfAttendees)
                        .append("</td></tr>");
            }
            if (numberOfRooms > 0) {
                bodyBuilder.append("<tr><td><strong>Number of Rooms</strong></td><td>").append(numberOfRooms)
                        .append("</td></tr>");
            }

            // Travel Details
            bodyBuilder.append("<tr><th colspan=\"2\"><h4>Travel Details</h4></th></tvehicleTyper>");
            bodyBuilder.append("<tr><td><strong>Source Location</strong></td><td>");
            bodyBuilder.append("<strong>Country:</strong> ").append(sourceCountry).append("<br>");
            bodyBuilder.append("<strong>State:</strong> ").append(sourceState).append("<br>");
            bodyBuilder.append("<strong>City:</strong> ").append(sourceCity).append("</td></tr>");
            bodyBuilder.append("<tr><td><strong>Destination Locations</strong></td><td>");

            bodyBuilder.append("<strong>States:</strong> ").append(String.join(", ", destinationStates)).append("<br>");
            bodyBuilder.append("<strong>Cities:</strong> ").append(String.join(", ", destinationCities))
                    .append("</td></tr>");

            // Transportation Details
            bodyBuilder.append("<tr><th colspan=\"2\"><h4>Transportation Details</h4></th></tr>");
            if (transportationRequired != null && !transportationRequired.isEmpty()) {
                bodyBuilder.append("<tr><td><strong>Transportation Required</strong></td><td>")
                        .append(transportationRequired).append("</td></tr>");
            } else {
                bodyBuilder.append("<tr><td><strong>Transportation Required</strong></td><td>N/A</td></tr>");
            }
            if (!vehicleType.isEmpty()) {
                bodyBuilder.append("<tr><td><strong>Preferred Vehicle Type</strong></td><td>").append(vehicleType)
                        .append("</td></tr>");
            }

            // Special Requests
            bodyBuilder.append("<tr><th colspan=\"2\"><h4>Special Requests</h4></th></tr>");
            if (!specialRequests.isEmpty()) {
                bodyBuilder.append("<tr><td colspan=\"2\">").append(specialRequests).append("</td></tr>");
            }
            // Closing statement
            bodyBuilder.append("</table>");
            bodyBuilder.append("<p style=\\\"color: brown;\\\"><i>Best regards,</i><br>");
            bodyBuilder.append("<b><i>VGR Events</i></b></p>");
            bodyBuilder.append("</body></html>");

            // Set the HTML content for the message
            message.setContent(bodyBuilder.toString(), "text/html");

            // Send the message
            Transport.send(message);

            System.out.println("Email Sent Successfully to " + to);
            return true; // Email was successfully sent
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            return false; // Email sending failed
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value); // Convert to int, may cause NumberFormatException
        } catch (NumberFormatException e) {
            return 0; // Return 0 for invalid input - you may want to handle this differently
        }
    }

    private String formatDate(String date) {
        // Implement date formatting logic here
        return date;
    }
}
