package codegen;

class CodeAarch64 implements Code {
    private final byte[] code;
    private int pos;

    private final StringBuilder assembly;

    public static final int SP = 31;

    public CodeAarch64() {
        code = new byte[64 * 1024];
        pos = 0;
        assembly = new StringBuilder();
    }

    public void save(String filename) {
        // open filename for writing and write the code byte array to it
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filename)) {
            fos.write(code, 0, pos);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        System.out.println("Generated assembly code into " + filename + ":");
        System.out.println(assembly.toString());
    }

    private void emit(byte b) {
        code[pos] = b;
        pos += 1;
    }

    private void emitInstruction(byte b1, byte b2, byte b3, byte b4) {
        emit(b4);
        emit(b3);
        emit(b2);
        emit(b1);
    }

    private String registerName(int reg) {
        if (reg == SP) {
            return "sp";
        }
        return "x" + reg;
    }

    public void emitLoadRegister(int targetReg, int baseReg, int offset) {
        assembly.append("ldr ").append(registerName(targetReg)).append(", [").append(registerName(baseReg)).append(", #").append(offset).append("]\n");
        emitInstruction((byte) 0xF9, (byte) (targetReg & 0x1F), (byte) (baseReg & 0x1F), (byte) ((offset >> 2) & 0xFF));
    }

    public void emitLoadPairOfRegisterPostIndexed(int reg1, int reg2, int baseReg, int offset) {
        assembly.append("ldp ").append(registerName(reg1)).append(", ").append(registerName(reg2)).append(", [").append(registerName(baseReg)).append("], #").append(offset).append("\n");
        emitInstruction((byte) 0xA8, (byte) ((reg1 & 0x1F) | ((reg2 & 0x1F) << 5)), (byte) (baseReg & 0x1F), (byte) ((offset >> 2) & 0xFF));
    }

    public void emitStorePairOfRegisterPreIndex(int reg1, int reg2, int baseReg, int offset) {
        assembly.append("stp ").append(registerName(reg1)).append(", ").append(registerName(reg2)).append(", [").append(registerName(baseReg)).append(", #").append(offset).append("]\n");
        emitInstruction((byte) 0xA9, (byte) ((reg1 & 0x1F) | ((reg2 & 0x1F) << 5)), (byte) (baseReg & 0x1F), (byte) ((offset >> 2) & 0xFF));
    }

    public void emitMove(int targetReg, int sourceReg) {
        assembly.append("mov ").append(registerName(targetReg)).append(", ").append(registerName(sourceReg)).append("\n");
        emitInstruction((byte) 0xAA, (byte) ((targetReg & 0x1F) | ((sourceReg & 0x1F) << 5)), (byte) 0, (byte) 0);
    }

    public void emitMoveImmediate(int targetReg, int immediate) {
        assembly.append("mov ").append(registerName(targetReg)).append(", #").append(immediate).append("\n");
        emitInstruction((byte) 0xB2, (byte) (targetReg & 0x1F), (byte) 0, (byte) (immediate & 0xFF));
    }

    public void emitStoreRegister(int sourceReg, int baseReg, int offset) {
        assembly.append("str ").append(registerName(sourceReg)).append(", [").append(registerName(baseReg)).append(", #").append(offset).append("]\n");
        emitInstruction((byte) 0xF8, (byte) (sourceReg & 0x1F), (byte) (baseReg & 0x1F), (byte) ((offset >> 2) & 0xFF));
    }

    public void emitBranchWithLinkToRegister(int targetReg) {
        assembly.append("blr ").append(registerName(targetReg)).append("\n");
        emitInstruction((byte) 0xD6, (byte) (targetReg & 0x1F), (byte) 0, (byte) 0);
    }

    public void emitReturn() {
        assembly.append("ret\n");
        emit((byte) 0xC0);
    }
}
