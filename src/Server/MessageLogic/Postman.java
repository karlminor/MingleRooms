package Server.MessageLogic;

import Server.UserLogic.User;
import Server.UserLogic.Users;

import java.io.IOException;

public class Postman extends Thread {
	Users users;
	Mailbox mailbox;

	public Postman(Users users, Mailbox mailbox) {
		this.users = users;
		this.mailbox = mailbox;
	}

	public void run() {
		Message message;
		User sender;
		String text;
		String[] info;
		int id;
		while (true) {
			try {
				message = mailbox.withdraw();
				sender = message.sender;
				text = message.text;
				for (User u : users.userList()) {
					/*
					 * Finds the identifier of the message and takes different actions depending on
					 * this N = new user connected, format is name A = avatar information, format is
					 * avatar identifier P = position update, the string received will be format
					 * xxxx¤yyyy should dx dy R = room update, the string received will be formatted
					 * as just the number for the room and the position i.e. Rid¤nn¤xxxx¤yyyy the
					 * user joining the room will receive information on all users in the room just
					 * joined formatted as Rid¤nn¤xxxx¤yyyy M = message, the string received will be
					 * formatted Mid¤text Q = quit
					 */

					if (u.status()) {
						char identifier = text.charAt(0);
						switch (identifier) {
						case ('P'):
							if (sender.sameRoom(u)) {
								u.echo(text);
							}
							break;
						case ('R'):
							if (sender.sameRoom(u)) {
								info = u.getInfo();
								sender.echo("R" + info[0] + "¤" + info[3] + "¤" + info[4] + "¤" + info[5]);
							}
							//u.echo(text);
							break;
						case ('M'):
							if (sender.sameRoom(u)) {
								u.echo(text);
							}
							break;
						case ('Q'):
							break;
						default:
							break;
						}

					}
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void notifyExitAll(User u) throws IOException {
		users.remove(u);
		u.interrupt();

		String[] info = u.getInfo();
		for (User user : users.userList()) {
			user.echo("Q" + info[0]);
		}
	}
}