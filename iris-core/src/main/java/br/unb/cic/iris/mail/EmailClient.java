package br.unb.cic.iris.mail;

import java.util.List;

import javax.mail.search.SearchTerm;

import org.apache.commons.validator.EmailValidator;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.core.model.IrisFolder;
import br.unb.cic.iris.mail.provider.GmailProvider;
import br.unb.cic.iris.persistence.sqlite3.AddressBookDAO;

public class EmailClient implements IEmailClient {
	public static final String CHARACTER_ENCODING = "UTF-8";

	private final EmailSender sender;
	private final EmailReceiver receiver;

	public EmailClient(String username, String password) {
		this(new GmailProvider(username, password));
	}

	public EmailClient(EmailProvider provider) {
		this(provider, CHARACTER_ENCODING);
	}

	public EmailClient(EmailProvider provider, String encoding) {
		// TODO validar entradas
		sender = new EmailSender(provider, encoding);
		receiver = new EmailReceiver(provider, encoding);
	}

	@Override
	public void send(EmailMessage email) throws EmailException {
		System.out.println("send message: " + email);
		
		//TODO: I think this would be necessary in the case 
		//where the AddressBook feature is enabled. 
		
		//email.setTo(setAddress(email.getTo());
		//email.setCc(setAddress(email.getCc());
		//email.setBcc(setAddress(email.getBccc());
		
		
		sender.send(email);
	}

	private String findAddress(String email) throws DBException {
		if(email != null && ! EmailValidator.getInstance().isValid(email)){
			return (AddressBookDAO.instance().find(email)).getAddress();
		}
		return email;
	}

	@Override
	public List<IrisFolder> listFolders() throws EmailException {
		System.out.println("listing folders ...");
		return receiver.listFolders();
	}

	@Override
	public List<EmailMessage> getMessages(String folder, SearchTerm searchTerm)
			throws EmailException {
		return receiver.getMessages(folder, searchTerm);
	}

	@Override
	public List<EmailMessage> getMessages(String folder, int seqnum) throws EmailException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<EmailMessage> getMessages(String folder, int begin, int end)
			throws EmailException {
		return receiver.getMessages(folder, begin, end);
	}

	@Override
	public List<String> validateEmailMessage(EmailMessage message) {
		// TODO ......
		return sender.validateEmailMessage(message);
	}

}
