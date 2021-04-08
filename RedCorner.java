
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;



@Autonomous(name = "RedCorner", group = "plms")
public class RedCorner extends AutoBase {
    public RedCorner()
    {
        autoConfig = new AutoConfig();
        autoConfig.sideColor = Detected_Color.Red;
        autoConfig.positionMiddle = false;

        autoConfig.cornerFirstMoveDistance = 12;
        autoConfig.cornerLeftTurnDistance = 5;
        autoConfig.cornerLeftDistance = 11;
        autoConfig.cornerCenterDistance = 6.5;
        autoConfig.cornerRightDistance = 3;

        autoConfig.faceCrypoBoxRightTurnDistance = 5.5;
    }
}
