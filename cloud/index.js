const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

const firestore = admin.firestore();


exports.onUserStatusChanged = functions.database.ref(uid).onUpdate(
	async (change, context) => {
		const eventStatus = change.after.val();
		const userRef = firestore.doc(`users/${context.params.uid}`);

		const statusSnapshot = await change.after.ref.once('value');
		const status = statusSnapshot.val();
		functions.logger.log(status, eventStatus);
	
		if (status.lastSeen > eventStatus.lastSeen) {
			return null;
		}

		return userRef.update({
			status: eventStatus.status,
			lastSeen: new Date(eventStatus.lastSeen)
		});
	});