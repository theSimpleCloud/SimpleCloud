/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.launcher.logging

import org.fusesource.jansi.Ansi

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.09.2019
 * Time: 17:38
 */
enum class AnsiColorHelper(val ansiName: String, val index: Char, val ansiCode: String) {

    RESET("reset", 'r', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.DEFAULT).boldOff().toString()),
    WHITE("white", 'f', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString()),
    BLACK("black", '0', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString()),
    RED("red", 'c', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString()),
    YELLOW("yellow", 'e', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString()),
    BLUE("blue", '9', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString()),
    GREEN("green", 'a', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString()),
    PURPLE("purple", '5', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString()),
    ORANGE("orange", '6', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString()),
    GRAY("gray", '7', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString()),
    DARK_RED("dark_red", '4', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString()),
    DARK_GRAY("dark_gray", '8', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString()),
    DARK_BLUE("dark_blue", '1', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString()),
    DARK_GREEN("dark_green", '2', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString()),
    LIGHT_BLUE("light_blue", 'b', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString()),
    CYAN("cyan", '3', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString());

    companion object {
        fun toColoredString(msg: String): String {
            var text = msg
            for (consoleColour in values()) {
                text = text.replace('§'.toString() + "" + consoleColour.index, consoleColour.ansiCode)
                text = text.replace('&'.toString() + "" + consoleColour.index, consoleColour.ansiCode)
            }

            return text
        }
    }

}