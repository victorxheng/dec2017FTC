
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;




@Autonomous(name = "RedMiddle", group = "plms")
public class RedMiddle extends AutoBase {
    public RedMiddle()
    {
        autoConfig = new AutoConfig();
        autoConfig.sideColor = Detected_Color.Red;
        autoConfig.positionMiddle = true;

        autoConfig.middleLeftDistance = 20.5;
        autoConfig.middleCenterDistance = 17;
        autoConfig.middleRightDistance = 13.5;

        autoConfig.faceCrypoBoxRightTurnDistance = 6.5;
    }
}
