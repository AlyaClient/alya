/*
 * Copyright (c) 2026 Alya Client.
 *
 * Alya Client is a free, open-source Minecraft hacked client.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package bypass.util.openauth.model.response;

@SuppressWarnings("unused")
public class XboxLoginResponse {
    private final String IssueInstant;
    private final String NotAfter;
    private final String Token;
    private final XboxLiveLoginResponseClaims DisplayClaims;

    public XboxLoginResponse(
            String IssueInstant,
            String NotAfter,
            String Token,
            XboxLiveLoginResponseClaims DisplayClaims) {
        this.IssueInstant = IssueInstant;
        this.NotAfter = NotAfter;
        this.Token = Token;
        this.DisplayClaims = DisplayClaims;
    }

    public String getIssueInstant() {
        return IssueInstant;
    }

    public String getNotAfter() {
        return NotAfter;
    }

    public String getToken() {
        return Token;
    }

    public XboxLiveLoginResponseClaims getDisplayClaims() {
        return DisplayClaims;
    }

    public static class XboxLiveLoginResponseClaims {
        private final XboxLiveUserInfo[] xui;

        public XboxLiveLoginResponseClaims(XboxLiveUserInfo[] xui) {
            this.xui = xui;
        }

        public XboxLiveUserInfo[] getUsers() {
            return xui;
        }
    }

    public static class XboxLiveUserInfo {
        private final String uhs;

        public XboxLiveUserInfo(String uhs) {
            this.uhs = uhs;
        }

        public String getUserHash() {
            return uhs;
        }
    }
}
