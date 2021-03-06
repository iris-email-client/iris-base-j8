package br.unb.cic.iris.mail;

import static br.unb.cic.iris.i18n.Message.message;
import static br.unb.cic.iris.util.StringUtil.isEmpty;
import static br.unb.cic.iris.util.StringUtil.notEmpty;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.exception.EmailMessageValidationException;
import br.unb.cic.iris.core.model.EmailMessage;

public class EmailSender implements TransportListener {
	// private static final Logger log =
	// Logger.getLogger(EmailSender.class.getName());

	private EmailSession session;
	private EmailProvider provider;

	public EmailSender(EmailProvider provider, String encoding) {
		this.provider = provider;
		session = new EmailSession(provider, encoding);
	}

	public void send(EmailMessage email) throws EmailException {
		List<String> errorMessages = validateEmailMessage(email);
		if (errorMessages.isEmpty()) {
			try {
				final Message message = createMessage(email);
				message.saveChanges(); // some headers and id's will be set for
				// this specific message

				// getSession().getProperties().forEach((k, v) ->
				// System.out.printf("   --> %s=%s%n",k,v));

				Transport transport = createTransport();

				session.connect(transport, provider.getTransportHost(),
						provider.getTransportPort());

				System.out.println("Sending message ...");
				transport.sendMessage(message, message.getAllRecipients());

				transport.close();
			} catch (final UnsupportedEncodingException e) {
				throw new  EmailException(message("error.invalid.encoding", e.getMessage()));
			} catch (final MessagingException e) {
				throw new  EmailException(message("error.send.email", e.getMessage()));
			}
		} else {
			throw new EmailMessageValidationException(errorMessages);
		}
	}

	// TODO criar classe validator separada
	public static List<String> validateEmailMessage(EmailMessage message) {
		List<String> errorMessages = new ArrayList<>();

		
		if (message == null) {
			errorMessages.add(message("error.null.message"));
		} else if (isEmpty(message.getFrom())) {
			// TODO terminar
			errorMessages.add(message("error.required.field",message("command.send.label.from")));
		}

		return errorMessages;
	}

	private Transport createTransport() throws MessagingException {
		System.out.println("Creating transport: "
				+ provider.getTransportProtocol());
		Transport transport = session.getSession().getTransport(
				provider.getTransportProtocol());
		transport.addTransportListener(this);
		transport.addConnectionListener(session);

		return transport;
	}

	private Message createMessage(final EmailMessage email)
			throws MessagingException, UnsupportedEncodingException {
		final MimeMessage message = new MimeMessage(session.getSession());

		message.setSubject(email.getSubject(), session.getEncoding());
		message.setFrom(new InternetAddress(email.getFrom(), session
				.getEncoding()));
		message.setRecipient(RecipientType.TO,
				new InternetAddress(email.getTo()));
		message.setText(email.getMessage(), session.getEncoding());

		if (notEmpty(email.getCc())) {
			message.setRecipient(RecipientType.CC,
					new InternetAddress(email.getCc()));
		}
		if (notEmpty(email.getBcc())) {
			message.setRecipient(RecipientType.BCC,
					new InternetAddress(email.getBcc()));
		}

		message.setSentDate(new Date());
		return message;
	}

	@Override
	public void messageDelivered(TransportEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Message delivered ... ");
	}

	@Override
	public void messageNotDelivered(TransportEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Message not delivered ... ");
	}

	@Override
	public void messagePartiallyDelivered(TransportEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Message partially delivered ... ");
	}

}
