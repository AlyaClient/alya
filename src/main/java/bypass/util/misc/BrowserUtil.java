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

package bypass.util.misc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BrowserUtil {
    private static final Logger logger = LogManager.getLogger(BrowserUtil.class);

    public static void open(String url) {
        String os = Platform.OS;
        try {
            if(os.contains("linux") || os.contains("unix")) {
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
            } else if(os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", url});
            } else if(os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } else {
                logger.error("Unsupported platform for opening URLs: {}", os);
            }
        } catch(Exception e) {
            logger.error("Couldn't open link: {}", url, e);
        }
    }
}
