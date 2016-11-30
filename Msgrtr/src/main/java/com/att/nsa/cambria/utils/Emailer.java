/*******************************************************************************
 * BSD License
 *  
 * Copyright (c) 2016, AT&T Intellectual Property.  All other rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *  
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. All advertising materials mentioning features or use of this software must display the
 *    following acknowledgement:  This product includes software developed by the AT&T.
 * 4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *******************************************************************************/
package com.att.nsa.cambria.utils;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.drumlin.till.nv.rrNvReadable;

/**
 * Send an email from a message.
 * 
 * @author author
 */
public class Emailer
{
	public static final String kField_To = "to";
	public static final String kField_Subject = "subject";
	public static final String kField_Message = "message";

	public Emailer()
	{
		fExec = Executors.newCachedThreadPool ();
	//	fSettings = settings;
	}

	public void send ( String to, String subj, String body ) throws IOException
	{
		final String[] addrs = to.split ( "," );

		if ( to.length () > 0 )
		{
			final MailTask mt = new MailTask ( addrs, subj, body );
			fExec.submit ( mt );
		}
		else
		{
			log.warn ( "At least one address is required." );
		}
	}

	public void close ()
	{
		fExec.shutdown ();
	}

	private final ExecutorService fExec;
	//private final rrNvReadable fSettings;

	//private static final Logger log = LoggerFactory.getLogger ( Emailer.class );

	private static final EELFLogger log = EELFManager.getInstance().getLogger(Emailer.class);
	
	public static final String kSetting_MailAuthUser = "mailLogin";
	public static final String kSetting_MailAuthPwd = "mailPassword";
	public static final String kSetting_MailFromEmail = "mailFromEmail";
	public static final String kSetting_MailFromName = "mailFromName";
	public static final String kSetting_SmtpServer = "mailSmtpServer";
	public static final String kSetting_SmtpServerPort = "mailSmtpServerPort";
	public static final String kSetting_SmtpServerSsl = "mailSmtpServerSsl";
	public static final String kSetting_SmtpServerUseAuth = "mailSmtpServerUseAuth";

	private class MailTask implements Runnable
	{
		public MailTask ( String[] to, String subject, String msgBody )
		{
			fToAddrs = to;
			fSubject = subject;
			fBody = msgBody;
		}

		private String getSetting ( String settingKey, String defval )
		{
			//return fSettings.getString ( settingKey, defval );
			String strSet = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,settingKey);
			if(strSet==null)strSet=defval;
			return strSet;
		}

		// we need to get setting values from the evaluator but also the channel config
		private void makeSetting ( Properties props, String propKey, String settingKey, String defval )
		{
			props.put ( propKey, getSetting ( settingKey, defval ) );
		}

		private void makeSetting ( Properties props, String propKey, String settingKey, int defval )
		{
			makeSetting ( props, propKey, settingKey, "" + defval );
		}

		private void makeSetting ( Properties props, String propKey, String settingKey, boolean defval )
		{
			makeSetting ( props, propKey, settingKey, "" + defval );
		}

		@Override
		public void run ()
		{
			final StringBuffer tag = new StringBuffer ();
			final StringBuffer addrList = new StringBuffer ();
			tag.append ( "(" );
			for ( String to : fToAddrs )
			{
				if ( addrList.length () > 0 )
				{
					addrList.append ( ", " );
				}
				addrList.append ( to );
			}
			tag.append ( addrList.toString () );
			tag.append ( ") \"" );
			tag.append ( fSubject );
			tag.append ( "\"" );
			
			log.info ( "sending mail to " + tag );

			try
			{
				final Properties prop = new Properties ();
				makeSetting ( prop, "mail.smtp.port", kSetting_SmtpServerPort, 587 );
				prop.put ( "mail.smtp.socketFactory.fallback", "false" );
				prop.put ( "mail.smtp.quitwait", "false" );
				makeSetting ( prop, "mail.smtp.host", kSetting_SmtpServer, "smtp.it.att.com" );
				makeSetting ( prop, "mail.smtp.auth", kSetting_SmtpServerUseAuth, true );
				makeSetting ( prop, "mail.smtp.starttls.enable", kSetting_SmtpServerSsl, true );

				final String un = getSetting ( kSetting_MailAuthUser, "" );
				final String pw = getSetting ( kSetting_MailAuthPwd, "" );
				final Session session = Session.getInstance ( prop,
					new javax.mail.Authenticator()
					{
						@Override
						protected PasswordAuthentication getPasswordAuthentication()
						{
							return new PasswordAuthentication ( un, pw );
						}
					}
				);
				
				final Message msg = new MimeMessage ( session );

				final InternetAddress from = new InternetAddress (
					getSetting ( kSetting_MailFromEmail, "team@sa2020.it.att.com" ),
					getSetting ( kSetting_MailFromName, "The GFP/SA2020 Team" ) );
				msg.setFrom ( from );
				msg.setReplyTo ( new InternetAddress[] { from } );
				msg.setSubject ( fSubject );

				for ( String toAddr : fToAddrs )
				{
					final InternetAddress to = new InternetAddress ( toAddr );
					msg.addRecipient ( Message.RecipientType.TO, to );
				}

				final Multipart multipart = new MimeMultipart ( "related" );
				final BodyPart htmlPart = new MimeBodyPart ();
				htmlPart.setContent ( fBody, "text/plain" );
				multipart.addBodyPart ( htmlPart );
				msg.setContent ( multipart );

				Transport.send ( msg );

				log.info ( "mailing " + tag + " off without error" );
			}
			catch ( Exception e )
			{
				log.warn ( "Exception caught for " + tag, e );
			}
		}

		private final String[] fToAddrs;
		private final String fSubject;
		private final String fBody;
	}
}
