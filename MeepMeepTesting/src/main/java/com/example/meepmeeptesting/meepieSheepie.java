package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.ColorManager;
import com.noahbres.meepmeep.core.colorscheme.ColorScheme;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class meepieSheepie {
    public static void main(String[] args) {
        MeepMeep meepoSheepo = new MeepMeep(800, 240);

        RoadRunnerBotEntity jermBot = new DefaultBotBuilder(meepoSheepo)
                .setDimensions(13,16)
                .setConstraints(52.48291908330528, 52.48291908330528, Math.toRadians(261.482587826087), Math.toRadians(261.482587826087), 11.5)
                .setColorScheme(new ColorScheme() {
                    @Override
                    public boolean isDark() {
                        return false;
                    }

                    @NotNull
                    @Override
                    public Color getBOT_BODY_COLOR() {
                        return ColorManager.COLOR_PALETTE.getBLUE_600();
                    }

                    @NotNull
                    @Override
                    public Color getBOT_WHEEL_COLOR() {
                        return ColorManager.COLOR_PALETTE.getBLUE_800();
                    }

                    @NotNull
                    @Override
                    public Color getBOT_DIRECTION_COLOR() {
                        return ColorManager.COLOR_PALETTE.getBLUE_800();
                    }

                    @NotNull
                    @Override
                    public Color getAXIS_X_COLOR() {
                        return ColorManager.COLOR_PALETTE.getGRAY_900();
                    }

                    @NotNull
                    @Override
                    public Color getAXIS_Y_COLOR() {
                        return ColorManager.COLOR_PALETTE.getGRAY_900();
                    }

                    @Override
                    public double getAXIS_NORMAL_OPACITY() {
                        return 0.4;
                    }

                    @Override
                    public double getAXIS_HOVER_OPACITY() {
                        return 0.8;
                    }

                    @NotNull
                    @Override
                    public Color getTRAJCETORY_PATH_COLOR() {
                        return ColorManager.COLOR_PALETTE.getGREEN_900();
                    }

                    @NotNull
                    @Override
                    public Color getTRAJECTORY_TURN_COLOR() {
                        return ColorManager.COLOR_PALETTE.getORANGE_600();
                    }

                    @NotNull
                    @Override
                    public Color getTRAJECTORY_MARKER_COLOR() {
                        return ColorManager.COLOR_PALETTE.getGREEN_600();
                    }

                    @NotNull
                    @Override
                    public Color getTRAJECTORY_SLIDER_BG() {
                        return ColorManager.COLOR_PALETTE.getGRAY_200();
                    }

                    @NotNull
                    @Override
                    public Color getTRAJECTORY_SLIDER_FG() {
                        return ColorManager.COLOR_PALETTE.getBLUE_600();
                    }

                    @NotNull
                    @Override
                    public Color getTRAJECTORY_TEXT_COLOR() {
                        return ColorManager.COLOR_PALETTE.getGRAY_900();
                    }

                    @NotNull
                    @Override
                    public Color getUI_MAIN_BG() {
                        return ColorManager.COLOR_PALETTE.getGRAY_200();
                    }
                })
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(-36, -64.625, Math.PI))
                                .lineToLinearHeading(new Pose2d(-64, -55, Math.toRadians(180)))
                                .build());
        try {
            meepoSheepo
                    .setBackground(ImageIO.read(new File("E:\\FTC\\Programming\\roadrunnerfreightfrenzy\\MeepMeepTesting\\src\\main\\java\\com\\example\\meepmeeptesting\\aesthetic-field.png")))
                    .setTheme(new ColorSchemeBlueDark())
                    .addEntity(jermBot)
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}