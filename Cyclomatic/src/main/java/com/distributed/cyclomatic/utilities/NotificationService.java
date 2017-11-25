package  com.distributed.cyclomatic.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class NotificationService {

	private JavaMailSender javaMailSender;
	
	@Autowired
	public NotificationService(JavaMailSender javaMailSender){
		this.javaMailSender = javaMailSender;
	}
	
	@Async
	public void sendNotification(String subject, String message, String emailAddress) throws MailException, InterruptedException {
        //Thread.sleep(10000);		
        System.out.println("Sending email...");        
        SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(emailAddress);
		mail.setFrom("sameep.anandniketan@gmail.com");
		mail.setSubject(subject);
		mail.setText(message);
		javaMailSender.send(mail);		
		System.out.println("Email Sent!");
	}
	
}