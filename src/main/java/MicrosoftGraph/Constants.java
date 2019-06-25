/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package MicrosoftGraph;

public class Constants {

    public static final String DEFAULT_IMAGE_FILENAME = "test.jpg";
    public static final String NETWORK_NAME = "Microsoft Azure Active Directory";
    public final static String CLIENT_ID = "927f21e2-44a5-48a1-8a63-0d3e6bd4b7f3";
    public final static String REDIRECT_URL = "https://login.microsoftonline.com/common/oauth2/nativeclient";
    public final static String SCOPES = "User.Read Sites.FullControl.All Sites.ReadWrite.All Sites.Read.All";
    public static final String SITE_ID = "diskenterprisesolutions.sharepoint.com,56ab9c8f-ccad-492d-a45d-09ec2788c60a,70a7ef7f-ba63-4db6-8c5c-326129422582";
    public static final String PROTECTED_RESOURCE_URL = "https://graph.microsoft.com/v1.0/sites/diskenterprisesolutions.sharepoint.com,b1b85f90-52ad-4c60-a1ce-b740797482d5,a80ce79c-2ab3-42e0-b91e-923332c20ae4/lists/4bdbf119-7849-4c26-92d3-126b7bf2d912/items";
    //https://graph.microsoft.com/v1.0/sites/diskenterprisesolutions.sharepoint.com,b1b85f90-52ad-4c60-a1ce-b740797482d5,a80ce79c-2ab3-42e0-b91e-923332c20ae4/lists/4bdbf119-7849-4c26-92d3-126b7bf2d912/items
    //CorpOp/ReqForm ID: diskenterprisesolutions.sharepoint.com,b1b85f90-52ad-4c60-a1ce-b740797482d5,a80ce79c-2ab3-42e0-b91e-923332c20ae4
    //List ID: 4bdbf119-7849-4c26-92d3-126b7bf2d912

    public static final String SUBJECT_TEXT = "Welcome to Microsoft Graph development for Java with the Connect sample";

    // The Microsoft Graph delegated permissions that you set in the application
    // registration portal must match these scope values.
    // Update this constant with the scope (permission) values for your application:
    public static final String MESSAGE_BODY = "<html><head>\n" +
            "<meta http-equiv=\'Content-Type\' content=\'text/html; charset=us-ascii\'>\n" +
            "<title></title>\n" +
            "</head>\n" +
            "<body style=\'font-family:calibri\'>\n" +
            "<h2>Congratulations!</h2>\n" +
            "<p>This is a message from the Microsoft Graph Connect Sample. You are well on your way to incorporating Microsoft Graph endpoints in your apps.</p><a href=%s>See the photo you just uploaded!</a>\n" +
            "<h3>What\'s next?</h3><ul>\n" +
            "<li>Check out <a href=\'https://developer.microsoft.com/graph\'>developer.microsoft.com/graph</a> to start building Microsoft Graph apps today with all the latest tools, templates, and guidance to get started quickly.</li>\n" +
            "<li>Use the <a href=\'https://developer.microsoft.com/graph/graph-explorer\'>Graph Explorer</a> to explore the rest of the APIs and start your testing.</li>\n" +
            "<li>Browse other <a href=\'https://github.com/search?p=5&amp;q=org%3Amicrosoftgraph+sample&amp;type=Repositories&amp;utf8=%E2%9C%93\'>samples on GitHub</a> to see more of the APIs in action.</li>\n" +
            "</ul>\n" +
            "<h3>Give us feedback</h3>\n" +
            "<p>If you have any trouble running this sample, please <a href=\'https://github.com/microsoftgraph/uwp-csharp-connect-sample/issues\'>\n" +
            "log an issue</a> on our repository.</p><p>For general questions about the Microsoft Graph API, post to <a href=\'https://stackoverflow.com/questions/tagged/microsoftgraph\'>Stack Overflow</a>. Make sure that your questions or comments are tagged with [microsoftgraph].</p>\n" +
            "<p>Thanks, and happy coding!<br>\n" +
            "&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;Your Microsoft Graph samples development team </p>\n" +
            "<div style=\'text-align:center; font-family:calibri\'>\n" +
            "<table style=\'width:100%; font-family:calibri\'>\n" +
            "<tbody>\n" +
            "<tr>\n" +
            "<td><a href=\'https://github.com/microsoftgraph/uwp-csharp-connect-sample\'>See on GitHub</a>\n" +
            "</td>\n" +
            "<td><a href=\'https://office365.uservoice.com\'>Suggest on UserVoice</a>\n" +
            "</td>\n" +
            "<td><a href=\'https://twitter.com/share?text=I%20just%20started%20developing%20apps%20for%20%23ASP.NET%20using%20the%20%23MicrosoftGraph%20Connect%20app%20%40OfficeDev&amp;amp;url=https://github.com/microsoftgraph/uwp-csharp-connect-sample\'>Share on Twitter</a>\n" +
            "</td>\n" +
            "</tr>\n" +
            "</tbody>\n" +
            "</table>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";

}