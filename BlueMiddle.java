
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;




@Autonomous(name = "BlueMiddle", group = "plms")
public class BlueMiddle extends AutoBase {
    public BlueMiddle()
    {
        autoConfig = new AutoConfig();
        autoConfig.sideColor = Detected_Color.Blue;
        autoConfig.positionMiddle = true;

        autoConfig.middleLeftDistance = -14.5;
        autoConfig.middleCenterDistance = -18;
        autoConfig.middleRightDistance = -21;

        autoConfig.faceCrypoBoxRightTurnDistance = 6.5;
    }
}
