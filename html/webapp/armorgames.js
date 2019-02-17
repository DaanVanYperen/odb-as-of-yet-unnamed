document.domain = "armorgames.com";
(function(){
    var ag = null;
    document.addEventListener("DOMContentLoaded", function(event) {
        var agiChecks = 0;
        function checkForAGI() {
            if (agiChecks > 1000) return;

            try {
                if (typeof parent.agi !== 'undefined') {
                    ag = new ArmorGames({
                        user_id: parent.apiAuth.user_id,
                        auth_token: parent.apiAuth.auth_token,
                        game_id: parent.apiAuth.game_id,
                        api_key: '94D80989-6F79-4BF7-98FA-87CD903986EC',
                        agi: parent.agi
                        //env: 'stage'
                    });
					
					ag.scoreboard.submit({
						scoreboardName: "score",
						score: 111,
						callback: function(​ data:​Object​ ):​void ​{
							if​ (​ data.​success ) {
								// Data was successfully saved
							} else {
								// The call failed to contact Armor Games.
								// You have the opportunity to fail gracefully.
								trace​(​ data.​error );
							}
						}
						});

                    // ... you can start doing AG requests
                } else {
                    agiChecks++;
                    window.setTimeout(checkForAGI, 250);
                }
            } catch(err) {
                agiChecks++;
                window.setTimeout(checkForAGI, 250);
            }
        }
        checkForAGI();
    });
})();