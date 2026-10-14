

!function () {

    function loadURL(url) {
        let oRequest = new XMLHttpRequest();
        oRequest.open('GET', url, false);
        oRequest.send(null);
        return oRequest.responseText;
    }

    var socket; //TODO improve this

    Ext.define('CMDBuildUI.view.custompages.chatplugin.ChatPlugin', {
        extend: 'Ext.panel.Panel',

        requires: [
            'CMDBuildUI.view.custompages.chatplugin.ChatPluginController',
            'CMDBuildUI.view.custompages.chatplugin.ChatPluginModel'
        ],

        mixins: [
            'CMDBuildUI.mixins.CustomPage'
        ],

        alias: 'widget.custompages-chatplugin',
        controller: 'custompages-chatplugin',
        viewModel: {
            type: 'custompages-chatplugin'
        },

        html: loadURL('app/view/custompages/chatplugin/chatclient.html'), //TODO improve this

        listeners: {
            destroy: function () {

                console.log('shutdown chat plugin');

                socket.close();
            },
            afterrender: function () {

                console.log('loading chat plugin');

                const localVideo = document.getElementById('cm_chatplugin_localVideo');
                const remoteVideo = document.getElementById('cm_chatplugin_remoteVideo');
                const communityExtContainer = document.getElementById('cm_chatplugin_communityExtContainer');
                const communityContainer = document.getElementById('cm_chatplugin_communityContainer');
                const clientInfoContainer = document.getElementById('cm_chatplugin_clientInfoContainer');
                const chatContainer = document.getElementById('cm_chatplugin_chatContainer');
                const closeButton = document.getElementById('cm_chatplugin_closeButton');
                const audioOnButton = document.getElementById('cm_chatplugin_audioOnButton');
                const audioOffButton = document.getElementById('cm_chatplugin_audioOffButton');
                const videoOnButton = document.getElementById('cm_chatplugin_videoOnButton');
                const videoOffButton = document.getElementById('cm_chatplugin_videoOffButton');

                let localStream;
                let connection;
                let clients;
                let client;
                let sessionInfo = {};

                let enableLocalVideo = true, enableLocalAudio = true;

                // EVENTBUS

                const eb_subscriptions = {};
                function eb_subscribe(eventType, listenerId, callback) {
                    if (!eb_subscriptions[eventType]) {
                        eb_subscriptions[eventType] = {};
                    }
                    eb_subscriptions[eventType][listenerId] = callback;
                }
                function eb_unsubscribe(eventType, listenerId) {
                    delete eb_subscriptions[eventType][listenerId];
                    if (Object.keys(eb_subscriptions[eventType]).length === 0) {
                        delete eb_subscriptions[eventType];
                    }
                }
                function eb_publish(eventType, arg) {
                    console.log(`publish event ${eventType}`, arg);
                    if (!eb_subscriptions[eventType]) {
                        return;
                    }
                    Object.keys(eb_subscriptions[eventType]).forEach(key => eb_subscriptions[eventType][key](arg));
                }

                /// WEBSOCKET MGT

                const websocketUrl = window.location.origin.replace(/^http/, 'ws') + window.location.pathname.replace(/ui\/?$/, 'services/websocket/v1/main');
                socket = new WebSocket(websocketUrl);
                socket.onmessage = e => handleWebsocketMessage(e);
                socket.onopen = function (e) {
                    console.log('websocket ready');
                    sendMessage('wrtc.community.join');
                };
                socket.onclose = e => console.log('websocket closed', e); //todo auto connection restore
                socket.onerror = e => console.log('websocket error', e); //todo auto connection restore
                function sendMessage(action, payload) {
                    let message = JSON.stringify(Object.assign({'_id': Date.now(), '_action': action, '_sender': sessionInfo.clientId}, payload || {}));
                    console.log(`send websocket message (${message.length} chars)`);
                    socket.send(message);
                }
                function sendMessageTo(action, clientId, payload) {
                    sendMessage(action, Object.assign({'_target': clientId}, payload || {}));
                }
                function sendMessageToPeer(action, payload) {
                    sendMessageTo(action, client.clientId, payload || {});
                }
                async function handleWebsocketMessage(e) {
                    try {
                        var message = JSON.parse(e.data);
                        switch (message._event) {
                            case  "wrtc.community.welcome":
                                console.log('websocket community welcome', message);
                                clientInfoContainer.innerHTML = `client id: ${message.clientId} (${message.username})`;
                                sessionInfo = {clientId: message.clientId, username: message.username};
                                break;
                            case  "wrtc.community.update":
                                console.log('websocket community update', message);
                                clients = JSON.parse(message.clients);
                                rebuildCommunityContainer();
                                break;
                            case  "wrtc.call.ring":
                                eb_publish('call_ring', message);
                                break;
                            case  "wrtc.call.hangup":
                                eb_publish('call_hangup', message);
                                break;
                            case "wrtc.call.accept":
                                eb_publish('call_accepted', message);
//                            createOutgoingConnection();
                                break;
                            case "wrtc.call.reject":
                                eb_publish('call_rejected', message);
                                break;
                            case "wrtc.call.leave":
                                if (connection && client && client.clientId === message._sender) {
                                    closeConnection();
                                }
                                break;
                            case  "wrtc.init.candidate":
                                if (connection && client && client.clientId === message._sender) {
                                    console.log(`received ice candidate`);
                                    await connection.addIceCandidate(new RTCIceCandidate(JSON.parse(message.candidate)));
                                } else {
                                    console.log(`discard ice candidate from ${message._sender}`);
                                }
                                break;
                            case  "wrtc.init.offer":
                                if (connection && client && client.clientId === message._sender) {
                                    console.log(`received offer`);
                                    connection.setRemoteDescription(new RTCSessionDescription(JSON.parse(message.description)));
                                    await sendAnswer(connection);
                                } else {
                                    console.log(`discard offer from ${message._sender}`);
                                }
                                break;
                            case  "wrtc.init.answer":
                                if (connection && client && client.clientId === message._sender) {
                                    console.log(`received answer`);
                                    await connection.setRemoteDescription(new RTCSessionDescription(JSON.parse(message.description)));
                                } else {
                                    console.log(`discard answer from ${message._sender}`);
                                }
                                break;
                        }
                    } catch (e) {
                        console.log(e);
                    }
                }

                /// STATE MGT

                let state_cleanup_callback;
                function state_cleanup() {
                    if (state_cleanup_callback) {
                        state_cleanup_callback();
                        state_cleanup_callback = null;
                    }
                }
                function state_cleanup_set(callback) {
                    state_cleanup_callback = callback;
                }

                /// CALL MGT

                function state_waitingForCall() {
                    state_cleanup();
                    chatContainer.classList.add('cm_chatplugin_hidden');
                    communityExtContainer.classList.remove('cm_chatplugin_hidden');
                    client = null;
                    eb_subscribe('call_ring', 'listener', function (message) {
                        state_incomingCall(message);
                    });
                    state_cleanup_set(function () {
                        eb_unsubscribe('call_ring', 'listener');
                    });
                }
                function state_incomingCall(message) {
                    state_cleanup();
                    eb_subscribe('call_ring', 'listener', m => sendMessageTo('wrtc.call.reject', m._sender));
                    client = {username: message.username, clientId: message._sender};
                    console.log(`incoming call from ${client.clientId} (${client.username})`);
                    var dialog = Ext.MessageBox.show({
                        title: 'Incoming call',
                        message: `incoming call from ${client.username}`,
                        buttons: Ext.Msg.YESNOCANCEL,
                        closable: false,
                        buttonText: {
                            yes: 'accept (with video)',
                            no: 'accept (audio only)',
                            cancel: 'reject'
                        },
                        fn: async function (buttonText) {
                            switch (buttonText) {
                                case 'yes':
                                    await setAudioVideoEnabled(true, true);
                                    await acceptCall();
                                    break;
                                case 'no':
                                    await setAudioVideoEnabled(true, false);
                                    await acceptCall();
                                    break;
                                case 'cancel':
                                    rejectCall();
                                    break;
                            }
                        }
                    });
                    eb_subscribe('call_hangup', 'listener', function (m) {
                        dialog.hide();
                        CMDBuildUI.util.Notifier.showInfoMessage(`<b>${client.username}</b> call cancelled`);
                        state_waitingForCall();
                    });
                    state_cleanup_set(function () {
                        eb_unsubscribe('call_ring', 'listener');
                        eb_unsubscribe('call_hangup', 'listener');
                    });
                }
                async function acceptCall() {
                    await state_incomingConnection();
                    sendMessageToPeer('wrtc.call.accept');
                }
                function rejectCall() {
                    sendMessageToPeer('wrtc.call.reject');
                    state_waitingForCall();
                }
                function cancelCall() {
                    sendMessageToPeer('wrtc.call.hangup');
                    state_waitingForCall();
                }
                async function state_startCall(c) {
                    state_cleanup();
                    client = c;
                    console.log(`call peer = ${JSON.stringify(client)}`);
                    eb_subscribe('call_ring', 'listener', m => sendMessageTo('wrtc.call.reject', m._sender));
                    cleanupConnection();//reset call
                    await startLocalStream();
                    var dialog = Ext.MessageBox.show({
                        title: 'Outgoing call',
                        message: `calling ${client.username}...`,
                        buttons: Ext.Msg.YES,
                        closable: false,
                        buttonText: {
                            yes: 'cancel'
                        },
                        fn: function (buttonText) {
                            switch (buttonText) {
                                case 'yes':
                                    cancelCall();
                                    break;
                            }
                        }
                    });
                    eb_subscribe('call_accepted', 'listener', function (m) {
                        if (m._sender === client.clientId) {
                            state_outgoingConnection();
                        }
                    });
                    eb_subscribe('call_rejected', 'listener', function (m) {
                        if (m._sender === client.clientId) {
                            CMDBuildUI.util.Notifier.showInfoMessage(`<b>${client.username}</b> rejected your call`);
                            state_waitingForCall();
                        }
                    });
                    sendMessageToPeer('wrtc.call.ring', {username: sessionInfo.username});
                    state_cleanup_set(function () {
//                        dialog.destroy();
                        dialog.hide();
                        eb_unsubscribe('call_ring', 'listener');
                        eb_unsubscribe('call_accepted', 'listener');
                        eb_unsubscribe('call_rejected', 'listener');
                    });
                }

                /// COMMUNITY MGT

                function createElement(html) {
                    var div = document.createElement('div');
                    div.innerHTML = html.trim();
                    return div.firstChild;
                }
                function rebuildCommunityContainer() {
                    if (clients.length === 0) {
                        communityContainer.innerHTML = 'no contacs online at the moment';
                    } else {
                        communityContainer.innerHTML = '';
                        clients.forEach(function (c) {
                            let div = createElement(`<div title="clientId: ${c.clientId}" style="" ><div style="width: 200px;display:inline-block;"><span style="font-family: FontAwesome; color: lightgray;">&#xf007;</span>&nbsp;&nbsp;&nbsp;${c.username}</div></div>`);
                            let audioCall = createElement(`<span style="cursor: pointer;margin-left: 8px;display:inline-block;" title="start audio call"><span style="font-family: FontAwesome; color: green">&#xf095;</span></span>`);
                            let videoCall = createElement(`<span style="cursor: pointer;margin-left: 6px;display:inline-block;" title="start video call"><span style="font-family: FontAwesome; color: green">&#xf03d;</span></span>`);
                            div.appendChild(audioCall);
                            div.appendChild(videoCall);
                            communityContainer.appendChild(div);
                            audioCall.onclick = async function () {
                                await setAudioVideoEnabled(true, false);
                                state_startCall(c);
                            };
                            videoCall.onclick = async function () {
                                await setAudioVideoEnabled(true, true);
                                state_startCall(c);
                            };
                        });
                    }
                }

                /// CONNECTION MGT

                async function state_incomingConnection() {
                    state_cleanup();
                    cleanupConnection();//reset call
                    chatContainer.classList.remove('cm_chatplugin_hidden');
                    communityExtContainer.classList.add('cm_chatplugin_hidden');
                    await startLocalStream();
                    createConnection();
                }
                async function state_outgoingConnection() {
                    state_cleanup();
                    cleanupConnection();//reset call
                    chatContainer.classList.remove('cm_chatplugin_hidden');
                    communityExtContainer.classList.add('cm_chatplugin_hidden');
                    console.log('create outgoing connection');
                    await startLocalStream();
                    createConnection();
                    sendOffer();
                }
                async function sendOffer() {
                    console.log('send sdp offer');
                    const offer = await connection.createOffer({offerToReceiveAudio: true, offerToReceiveVideo: true});
                    sendMessageToPeer('wrtc.init.offer', {'description': JSON.stringify(offer)});
                    await connection.setLocalDescription(offer);
                }
                async function sendAnswer(connection) {
                    const answer = await connection.createAnswer();
                    console.log('send sdp answer', JSON.stringify(answer));
                    sendMessageToPeer('wrtc.init.answer', {'description': JSON.stringify(answer)});
                    await connection.setLocalDescription(answer);
                }
                function terminateCall() {
                    sendMessageToPeer('wrtc.call.leave');
                    closeConnection();
                }
                function createConnection() {
                    console.log('create connection');
                    try {
                        connection = new RTCPeerConnection({});
                        connection.addEventListener('icecandidate', e => sendIceCandidate_safe(e));
                        connection.addEventListener('negotiationneeded', function (e) {
                            try {
                                console.log(`received renegotiation needed event, connection state = ${connection.connectionState}`);
                                if (connection.connectionState === 'connected') {
                                    console.log('renegotiation needed');
                                    sendOffer();
                                }
                            } catch (e) {
                                console.log(e);
                            }
                        });
                        connection.addEventListener('iceconnectionstatechange', e => console.log(`ice state = ${connection.iceConnectionState}`));
                        connection.addEventListener('connectionstatechange', function (e) {
                            switch (connection.connectionState) {
                                case "disconnected":
                                    console.log('disconnected');
                                    closeConnection();
                                    break;
                                case "failed":
                                    console.log('connection failure');
                                    closeConnection();
                                    break;
                                case "closed":
                                    console.log('connection closed');
                                    closeConnection();
                                    break;
                                case "connected":
                                    console.log('connected');
                                    break;
                            }
                        });
                        connection.addEventListener('track', function (e) {
                            if (remoteVideo.srcObject !== e.streams[0]) {
                                console.log('open remote stream');
                                remoteVideo.srcObject = null;
                                remoteVideo.srcObject = e.streams[0];
                                e.streams[0].onremovetrack = function () {
                                    console.log('close remote stream');
                                    remoteVideo.srcObject = null;
                                };
                            }
                        });
                        attachStream_safe();
                        console.log('connection ready');
                    } catch (e) {
                        CMDBuildUI.util.Notifier.showErrorMessage(`error creating connection: ${e}`);
                        terminateCall();
                    }
                }
                function sendIceCandidate_safe(event) {
                    try {
                        if (event.candidate) {
                            console.log('send ice candidate');
                            sendMessageToPeer('wrtc.init.candidate', {'candidate': JSON.stringify(event.candidate.toJSON())});
                        }
                    } catch (e) {
                        console.log(e);
                    }
                }
                function cleanupConnection() {
                    remoteVideo.srcObject = null;
                    if (connection && connection.connectionState !== 'closed') {
                        console.log('close connection');
                        try {
                            connection.close();
                        } catch (e) {
                            console.log(e);
                        }
                    }
                    connection = null;
                    stopLocalStream();
                }
                function closeConnection() {
                    cleanupConnection();
                    CMDBuildUI.util.Notifier.showInfoMessage(`closed connection with <b>${client.username}</b>`);
                    state_waitingForCall();
                }

                /// STREAM MGT

                async function startLocalStream() {
                    try {
                        if (!localStream) {
                            console.log('start local stream');
                            if(!navigator.mediaDevices){
                                throw 'microphone/camera api not available';
                            }
                            localStream = await navigator.mediaDevices.getUserMedia({audio: true, video: enableLocalVideo});
                            localVideo.srcObject = localStream;
                            localStream.getTracks().forEach(track => track.onended = n => stopLocalStream());
                            attachStream_safe();
                        }
                    } catch (e) {
                        CMDBuildUI.util.Notifier.showErrorMessage(`error starting audio/video stream: ${e}`);
                    }
                }
                async function stopLocalStream() {
                    try {
                        if (localStream) {
                            console.log('stop local stream');
                            localVideo.srcObject = null;
                            localStream.getTracks().forEach(track => track.stop());
                            localStream = null;
                            attachStream_safe();
                        }
                    } catch (e) {
                        console.log(e);
                    }
                }
                async function reloadLocalStream() {
                    stopLocalStream();
                    await startLocalStream();
                }
                function attachStream_safe() {
                    if (connection) {
                        connection.getSenders().forEach(sender => connection.removeTrack(sender));//TODO improve this
                    }
                    if (localStream && connection) {
                        console.log('add local stream tracks');
                        if (enableLocalVideo) {
                            localStream.getVideoTracks().forEach(track => connection.addTrack(track, localStream));
                        }
                        if (enableLocalAudio) {
                            localStream.getAudioTracks().forEach(track => connection.addTrack(track, localStream));
                        }
                    }
                }
                async function setAudioEnabled(enabled) {
                    enableLocalAudio = enabled;
                    await reloadLocalStream();
                    updateAudioVideoControls();
                }
                async function setVideoEnabled(enabled) {
                    enableLocalVideo = enabled;
                    await reloadLocalStream();
                    updateAudioVideoControls();
                }
                async function setAudioVideoEnabled(a, v) {
                    enableLocalAudio = a;
                    enableLocalVideo = v;
                    await reloadLocalStream();
                    updateAudioVideoControls();
                }
                function updateAudioVideoControls() {
                    if (enableLocalVideo) {
                        videoOnButton.classList.add('cm_chatplugin_hidden');
                        videoOffButton.classList.remove('cm_chatplugin_hidden');
                    } else {
                        videoOnButton.classList.remove('cm_chatplugin_hidden');
                        videoOffButton.classList.add('cm_chatplugin_hidden');
                    }
                    if (enableLocalAudio) {
                        audioOnButton.classList.add('cm_chatplugin_hidden');
                        audioOffButton.classList.remove('cm_chatplugin_hidden');
                    } else {
                        audioOnButton.classList.remove('cm_chatplugin_hidden');
                        audioOffButton.classList.add('cm_chatplugin_hidden');
                    }
                }

                /// INIT

                audioOnButton.addEventListener('click', e => setAudioEnabled(true));
                audioOffButton.addEventListener('click', e => setAudioEnabled(false));
                videoOnButton.addEventListener('click', e => setVideoEnabled(true));
                videoOffButton.addEventListener('click', e => setVideoEnabled(false));
                closeButton.addEventListener('click', e => terminateCall());

                updateAudioVideoControls();

                state_waitingForCall();
                
                if (!window.isSecureContext) {
                    CMDBuildUI.util.Notifier.showErrorMessage('this page was not loaded from a secure context: microphone/camera access may be restricted');
                };

            }
        }
    });
}();
