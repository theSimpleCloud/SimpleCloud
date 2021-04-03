/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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

package eu.thesimplecloud.launcher.screens.session

import eu.thesimplecloud.launcher.screens.IScreen

/**
 * Created by IntelliJ IDEA.
 * Date: 21.09.2020
 * Time: 13:29
 * @author Frederick Baier
 */
class ScreenSession(
        val screen: IScreen,
        val screenCloseBehaviour: ScreenCloseBehaviour
) {


    enum class ScreenCloseBehaviour {

        /**
         * Closes the screen after the service the screen belongs to closes
         */

        CLOSE,

        /**
         * Keeps the screen open until "leave" was typed into the console
         */
        KEEP_OPEN,

        /**
         * Reopens the screen if the service starts again
         */
        REOPEN;


    }

}