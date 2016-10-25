# ![](images/logo.png)

### A sign editing plugin **by RezzedUp**

&nbsp;

## ![Features](images/features.png)

* **Modify placed signs.**
  * Instead of rewriting an entire sign, you can replace single lines with a simple command: **/sign set**

* **Copy and paste signs.**
  * Everyone is given a SignManager clipboard for copying and pasting signs.
  * There is no limit to how many times a sign may be pasted.

* **Permission checking.**
  * It's safe to let players use features from this plugin since it checks build permissions before applying changes to signs.

* **Colorful signs.**
  * Convert color codes (using the & symbol) to colors for players with the **signmanager.colors** permission.
  * *Great donor perk.*

&nbsp;

## ![Commands](images/commands.png)

**/sign set** *\<line number>* *\<text>*
* Set a specific line of a sign. Click on a sign to apply the change.
  * **Example:** `/sign set 1 This is line #1!`
    * *Sets the first line of a sign to "This is line #1!"*
  * **Example:** `/sign set 3 &bI &3love &1this`
    * *Sets the third line of a sign to "&bI &3love &1this"*
    * *If the player also has the **signmanager.colors** permision, the color codes will be converted automatically.*

**/sign copy** *[optional: \<pastes>]*
* Copy an entire sign to your SignManager clipboard. Click on another sign to paste.
  * **Example:** `/sign copy`
* The optional 'pastes' argument is how many times the copied content should be pasted.
  * **Example:** `/sign copy 3`
    * *Paste the copied sign 3 times*
* Setting the pastes argument to a negative number allows you to paste unlimited times.
  * **Example:** `/sign copy -1`
    * *Pastes the copied sign until the server restarts or you do `/sign cancel`*

**/sign copyline** *\<line number> *[optional: \<pastes>]*
* Copy a specific line to your SignManager clipboard. Pastes will only include the copied line.
  * **Example:** `/sign copyline 2`
    * *Copies the second line of a sign to your clipboard.*

**/sign cancel**
* Cancels sign copying/pasting and clears your SignManager clipboard.
* This is the only way to stop unlimited pasting.

&nbsp;

## ![Permissions](images/permissions.png)

Players with **op** automatically have all necessary permissions to use this plugin and its features.

* **signmanager.colors**
  * Allows players to use & color codes on their signs
* **signmanager.command**
  * Gives access to the /sign command
* **signmanager.***
  * All SignManager permissions