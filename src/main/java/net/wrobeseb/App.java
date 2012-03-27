package net.wrobeseb;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import microsoft.exchange.webservices.data.EmailMessage;
import microsoft.exchange.webservices.data.EventType;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.FindItemsResults;
import microsoft.exchange.webservices.data.Folder;
import microsoft.exchange.webservices.data.FolderId;
import microsoft.exchange.webservices.data.Item;
import microsoft.exchange.webservices.data.ItemView;
import microsoft.exchange.webservices.data.MessageBody;
import microsoft.exchange.webservices.data.NotificationEvent;
import microsoft.exchange.webservices.data.NotificationEventArgs;
import microsoft.exchange.webservices.data.StreamingSubscription;
import microsoft.exchange.webservices.data.StreamingSubscriptionConnection;
import microsoft.exchange.webservices.data.SubscriptionErrorEventArgs;
import microsoft.exchange.webservices.data.WebCredentials;
import microsoft.exchange.webservices.data.WebProxy;
import microsoft.exchange.webservices.data.WellKnownFolderName;
import microsoft.exchange.webservices.data.StreamingSubscriptionConnection.INotificationEventDelegate;
import microsoft.exchange.webservices.data.StreamingSubscriptionConnection.ISubscriptionErrorDelegate;


/**
 * Hello world!
 *
 */
public class App implements INotificationEventDelegate, ISubscriptionErrorDelegate
{
    public static void main( String[] args )
    {
    	//System.setProperty("javax.net.debug", "all");
    	
    	App app = new App();
    	
    	 TrustManager[] trustAllCerts = new TrustManager[] {
    		new X509TrustManager() {
				
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				
				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1)
						throws CertificateException {
					
				}
				
				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1)
						throws CertificateException {
					
				}
			}};

         try {
             SSLContext sc = SSLContext.getInstance("SSL");
             sc.init(null, trustAllCerts, new java.security.SecureRandom());
             HttpsURLConnection
                     .setDefaultSSLSocketFactory(sc.getSocketFactory());
         } catch (Exception e) {
         }
    	
         ExchangeService service = new ExchangeService();
         
         ExchangeCredentials cred = new WebCredentials("test", "test");
         
         service.setCredentials(cred);

        try {
        	service.setUrl(new URI("test"));

	        //service.setTraceEnabled(true);
	        
	        WellKnownFolderName sd = WellKnownFolderName.Inbox;
	        FolderId folderId = new FolderId(sd);

	        List<FolderId> folder = new ArrayList<FolderId>();
	        folder.add(folderId);
	        
	        StreamingSubscription subs = service.subscribeToStreamingNotifications(folder, EventType.NewMail);
	        
	        StreamingSubscriptionConnection conn = new StreamingSubscriptionConnection(service, 30);
	        conn.addSubscription(subs);
	        conn.addOnNotificationEvent(app);
	        conn.addOnDisconnect(app);
	        conn.open();
	        
	        EmailMessage msg= new EmailMessage(service);
	        msg.setSubject("From EWS");
	        msg.setBody(MessageBody.getMessageBodyFromText("Using the Microsoft EWS Managed API"));
	        msg.getToRecipients().add("test");
	        msg.send();
	        
	        Thread.sleep(20000);
	        conn.close();
	        System.out.println("end........");
	        
	      
	        //Folder inbox = Folder.bind(service, WellKnownFolderName.Inbox);
	       // ItemView view = new ItemView (10);
	       // FindItemsResults<Item> findResults = service.findItems(inbox.getId(),view);
	       // 			
        	//for(Item item : findResults.getItems()) {
        	//	System.out.println(item.getSubject());
    		//}
	        
        } catch (Exception e) {
			e.printStackTrace();
		}
    }

	@Override
	public void subscriptionErrorDelegate(Object arg0, SubscriptionErrorEventArgs arg1) {
		System.out.println("disconnecting==========");
	}

	@Override
	public void notificationEventDelegate(Object arg0, NotificationEventArgs arg1) {
		System.out.println("hi notification event==========");
	}
}
