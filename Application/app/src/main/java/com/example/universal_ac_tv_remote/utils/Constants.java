package com.example.universal_ac_tv_remote.utils;

import java.util.UUID;

public class Constants {

    public static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final UUID ESP32_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String ESP32_MAC_ADDRESS = "B0:B2:1C:44:C0:CE";

    // Enum za AC uredjaje
    public enum DecodeTypeAC {
        UNKNOWN(-1),
        COOLIX(15),
        DAIKIN(16),
        KELVINATOR(18),
        MITSUBISHI_AC(20),
        GREE(24),
        ARGO(27),
        TROTEC(28),
        TOSHIBA_AC(32),
        FUJITSU_AC(33),
        MIDEA(34),
        CARRIER_AC(37),
        HAIER_AC(38),
        HITACHI_AC(40),
        HITACHI_AC1(41),
        HITACHI_AC2(42),
        HAIER_AC_YRW02(44),
        WHIRLPOOL_AC(45),
        SAMSUNG_AC(46),
        ELECTRA_AC(48),
        PANASONIC_AC(49),
        DAIKIN2(53),
        VESTEL_AC(54),
        TECO(55),
        TCL112AC(57),
        MITSUBISHI_HEAVY_88(59),
        MITSUBISHI_HEAVY_152(60),
        DAIKIN216(61),
        SHARP_AC(62),
        GOODWEATHER(63),
        DAIKIN160(65),
        NEOCLIMA(66),
        DAIKIN176(67),
        DAIKIN128(68),
        AMCOR(69),
        DAIKIN152(70),
        HITACHI_AC424(73),
        HITACHI_AC3(77),
        DAIKIN64(78),
        AIRWELL(79),
        DELONGHI_AC(80),
        CARRIER_AC40(83),
        CARRIER_AC64(84),
        HITACHI_AC344(85),
        CORONA_AC(86),
        MIDEA24(87),
        SANYO_AC(89),
        VOLTAS(90),
        TECHNIBEL_AC(93),
        PANASONIC_AC32(96),
        ECOCLIM(98),
        TRUMA(100),
        HAIER_AC176(101),
        TEKNOPOINT(102),
        KELON(103),
        TROTEC_3550(104),
        SANYO_AC88(105),
        RHOSS(108),
        AIRTON(109),
        COOLIX48(110),
        HITACHI_AC264(111),
        KELON168(112),
        HITACHI_AC296(113),
        DAIKIN200(114),
        HAIER_AC160(115),
        CARRIER_AC128(116),
        TOTO(117),
        CLIMABUTLER(118),
        TCL96AC(119),
        BOSCH144(120),
        SANYO_AC152(121),
        DAIKIN312(122),
        CARRIER_AC84(125),
        YORK(126),
        BLUESTARHEAVY(127);

    private final int value;

        DecodeTypeAC(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    // Enum za TV uredjaje
    public enum DecodeTypeTV {
        UNKNOWN(-1),
        UNUSED(0),
        RC5(1),
        RC6(2),
        NEC(3),
        SONY(4),
        PANASONIC(5),
        JVC(6),
        SAMSUNG(7),
        WHYNTER(8),
        AIWA_RC_T501(9),
        LG(10),
        SANYO(11),
        MITSUBISHI(12),
        DISH(13),
        SHARP(14),
        COOLIX(15),
        DENON(17),
        SHERWOOD(19),
        RCMM(21),
        SANYO_LC7461(22),
        RC5X(23),
        PRONTO(25),
        NEC_LIKE(26),
        ARGO(27),
        NIKAI(29),
        RAW(30),
        GLOBALCACHE(31),
        MAGIQUEST(35),
        LASERTAG(36),
        GICABLE(43),
        LUTRON(47),
        PIONEER(50),
        LG2(51),
        MWM(52),
        SAMSUNG36(56),
        LEGOPF(58),
        SONY_38K(74),
        EPSON(75),
        DOSHISHA(81),
        MULTIBRACKETS(82),
        ZEPEAL(88),
        METZ(91),
        TRANSCOLD(92),
        MIRAGE(94),
        ELITESCREENS(95),
        MILESTAG2(97),
        XMP(99),
        BOSE(106),
        ARRIS(107),
        WOWWEE(124);

    private final int value;

        DecodeTypeTV(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
