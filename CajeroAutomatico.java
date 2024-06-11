import java.util.HashMap;
import java.util.Scanner;

// Custom exceptions
class SaldoEfectivoInsuficiente extends Exception {
    public SaldoEfectivoInsuficiente(String message) {
        super(message);
    }
}

class SaldoCuentaInsuficiente extends Exception {
    public SaldoCuentaInsuficiente(String message) {
        super(message);
    }
}

class Cuenta {
    private String numeroCuenta;
    private String pin;
    private double saldo;

    public Cuenta(String numeroCuenta, String pin, double saldo) {
        this.numeroCuenta = numeroCuenta;
        this.pin = pin;
        this.saldo = saldo;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public String getPin() {
        return pin;
    }

    public double getSaldo() {
        return saldo;
    }

    public void depositar(double monto) {
        saldo += monto;
    }

    public void retirar(double monto) throws SaldoCuentaInsuficiente {
        if (monto > saldo) {
            throw new SaldoCuentaInsuficiente("Saldo insuficiente en la cuenta.");
        }
        saldo -= monto;
    }

    public void transferir(Cuenta destino, double monto) throws SaldoCuentaInsuficiente {
        if (monto > saldo) {
            throw new SaldoCuentaInsuficiente("Saldo insuficiente en la cuenta.");
        }
        saldo -= monto;
        destino.depositar(monto);
    }
}

public class CajeroAutomatico {
    private static double saldoEfectivo = 100000;
    private static HashMap<String, Cuenta> cuentas = new HashMap<>();

    public static void main(String[] args) {
        // Crear cuentas de ejemplo
        cuentas.put("12345", new Cuenta("12345", "1234", 5000));
        cuentas.put("67890", new Cuenta("67890", "5678", 3000));

        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese su número de cuenta: ");
        String numeroCuenta = scanner.nextLine();
        System.out.print("Ingrese su PIN: ");
        String pin = scanner.nextLine();

        Cuenta cuenta = autenticar(numeroCuenta, pin);

        if (cuenta == null) {
            System.out.println("Autenticación fallida.");
            return;
        }

        while (true) {
            System.out.println("\nSeleccione una operación:");
            System.out.println("1. Ver saldo");
            System.out.println("2. Depositar en cuenta propia");
            System.out.println("3. Depositar en otras cuentas");
            System.out.println("4. Transferir a otras cuentas");
            System.out.println("5. Retirar efectivo");
            System.out.println("6. Salir");
            int opcion = scanner.nextInt();

            try {
                switch (opcion) {
                    case 1:
                        System.out.println("Saldo actual: " + cuenta.getSaldo());
                        break;
                    case 2:
                        System.out.print("Ingrese el monto a depositar: ");
                        double montoDeposito = scanner.nextDouble();
                        cuenta.depositar(montoDeposito);
                        System.out.println("Depósito exitoso. Saldo actual: " + cuenta.getSaldo());
                        break;
                    case 3:
                        System.out.print("Ingrese el número de cuenta destino: ");
                        String cuentaDestino = scanner.next();
                        System.out.print("Ingrese el monto a depositar: ");
                        double montoDepositoOtra = scanner.nextDouble();
                        if (cuentas.containsKey(cuentaDestino)) {
                            cuentas.get(cuentaDestino).depositar(montoDepositoOtra);
                            System.out.println("Depósito exitoso en la cuenta destino.");
                        } else {
                            System.out.println("Cuenta destino no encontrada.");
                        }
                        break;
                    case 4:
                        System.out.print("Ingrese el número de cuenta destino: ");
                        String cuentaTransferencia = scanner.next();
                        System.out.print("Ingrese el monto a transferir: ");
                        double montoTransferencia = scanner.nextDouble();
                        if (cuentas.containsKey(cuentaTransferencia)) {
                            cuenta.transferir(cuentas.get(cuentaTransferencia), montoTransferencia);
                            System.out.println("Transferencia exitosa. Saldo actual: " + cuenta.getSaldo());
                        } else {
                            System.out.println("Cuenta destino no encontrada.");
                        }
                        break;
                    case 5:
                        System.out.print("Ingrese el monto a retirar: ");
                        double montoRetiro = scanner.nextDouble();
                        if (montoRetiro > saldoEfectivo) {
                            throw new SaldoEfectivoInsuficiente("Saldo insuficiente en el cajero.");
                        }
                        cuenta.retirar(montoRetiro);
                        saldoEfectivo -= montoRetiro;
                        System.out.println("Retiro exitoso. Saldo actual: " + cuenta.getSaldo());
                        break;
                    case 6:
                        System.out.println("Gracias por usar nuestro cajero automático.");
                        return;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (SaldoCuentaInsuficiente | SaldoEfectivoInsuficiente e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static Cuenta autenticar(String numeroCuenta, String pin) {
        if (cuentas.containsKey(numeroCuenta) && cuentas.get(numeroCuenta).getPin().equals(pin)) {
            return cuentas.get(numeroCuenta);
        }
        return null;
    }
}
