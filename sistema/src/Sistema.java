import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.awt.Color;
import java.awt.Component;
import javax.swing.*;

// O SISTEMA ABAIXO FOI FEITO NO VSCODE, RECOMENDO USAR O MESMO EM CASO DE TESTE

class MsgException extends RuntimeException {
public MsgException(String message) {
super(message);
}

}

class Roupa implements Serializable{
protected int quantidadeDisponivel;
protected double preco;

public Roupa( double preco , int quantidadeDisponivel ) {
this.quantidadeDisponivel = quantidadeDisponivel;
this.preco = preco;
}

public double getPreco() {
return this.preco;
}

public int getQuantidadeDisponivel() {
return this.quantidadeDisponivel;
}

public void reabastecer(int quantidade) { 
this.quantidadeDisponivel += quantidade;
}
  
public String descricao() {
DecimalFormat d = new DecimalFormat("0.00");
return this.quantidadeDisponivel + ", " + d.format(this.preco) ;
}

public String getDescricao() {
    return this.getClass().getSimpleName() + ": " + descricao();
}

}

class Camisa extends Roupa {
private String tipo;
private String marca;
private String tamanho; 
private String cor;


public Camisa( String tipo, String cor, String marca, String tamanho, int quantidadeDisponivel, double preco) {
super(preco, quantidadeDisponivel);
this.tipo = tipo;
this.marca = marca;
this.tamanho = tamanho;
this.cor = cor;
}

public String getTipo() {
  return this.tipo;
}

public String getMarca() {
return marca;
}

public String getCor() {
return this.cor;
}

public String getTamanho() {
return tamanho;
}

@Override
public String descricao() {
return this.tipo  + ", "  + this.tamanho + ", " + this.marca + ", "
       + this.cor + ", " + super.descricao();
}

}

class Short extends Roupa {
private String tipo;
private String marca;
private int tamanho;
private String cor; 

public Short( String tipo, String cor, String marca, int tamanho, int quantidadeDisponivel, double preco) {
super(preco, quantidadeDisponivel);
this.tipo = tipo;
this.marca = marca;
this.tamanho = tamanho;
this.cor = cor;
}

public String getTipo() {
return this.tipo;
}

public String getMarca() {
return marca;
}

public String getCor() {
return this.cor;
}

public int getTamanho() {
return this.tamanho;
}

@Override
public String descricao() {
return this.tipo + ", " + this.tamanho + ", " + this.marca + ", "
      + this.cor + ", "  + super.descricao();
}

}

class Intimo extends Roupa {
private String tipo;
private String marca;
private String tamanho;
private String cor; 

public Intimo( String tipo,String cor, String marca, String tamanho, int quantidadeDisponivel, double preco ) {
super(preco, quantidadeDisponivel);
this.tipo = tipo;
this.marca = marca;
this.tamanho = tamanho;
this.cor = cor;
}

public String getTipo() {
return this.tipo;
}

public String getMarca() {
return marca;
}

public String getCor() {
return this.cor;
}

public String getTamanho() {
return tamanho;
}

@Override
public String descricao() {
return this.tipo + ", " + this.tamanho + ", " + this.marca + ", "
      + this.cor + ", "  + super.descricao();
}

}

enum FormaPagamento {
PIX("Pix"),
DINHEIRO("Dinheiro"),
CARTAO("Cartão");

private String descricao;

private FormaPagamento( String descricao ) {
this.descricao = descricao;
}

public String getFormaPagamento() {
return this.descricao;
}

}

class Data implements Serializable{
private int dia;
private int mes;
private int ano;

public Data( int dia, int mes, int ano ) {
this.dia = dia;
this.mes = mes;
this.ano = ano;
}

public int getDia() {
return this.dia;
}

public int getMes() {
return this.mes;
}

public int getAno() {
return this.ano;
}

@Override
public boolean equals( Object object ) {
if (this == object) { return true; }

if (object == null || getClass() != object.getClass()) { return false; }

Data data = ( Data ) object;
return dia == data.dia && mes == data.mes && ano == data.ano;
}

@Override
public int hashCode() {
return Objects.hash( dia, mes, ano );
}

@Override
public String toString() {
return String.format( "%02d/%02d/%04d", this.dia, this.mes, this.ano );
}

}

class ItemPedido implements Serializable{
private Map<Roupa, Integer> roupas;
private FormaPagamento formaPagamento;

public ItemPedido( FormaPagamento formaPagamento ) {
this.roupas = new HashMap<Roupa, Integer>();
this.formaPagamento = formaPagamento;
}

public int getQuantidade() {
int quantidadeTotal = 0;

for (int quantidade : this.roupas.values()) {
quantidadeTotal += quantidade;
}

return quantidadeTotal;
}

public double getValorTotal() {
double total = 0;

for ( Map.Entry<Roupa, Integer> roupa : this.roupas.entrySet() ) {
total += roupa.getKey().getPreco() * roupa.getValue();
}

return total;
}

public void adicionarRoupa( Roupa roupa, int quantidade ) {
if ( roupa == null ) {
throw new MsgException("Falha: Roupa não pode ser nula");
}

if ( quantidade <= 0 ) {
throw new MsgException("Falha: Quantidade deve ser positiva");
}

if (roupa.getQuantidadeDisponivel() < quantidade) {
throw new MsgException("Falha: Estoque insuficiente para " + roupa.getDescricao());
}

this.roupas.put( roupa, this.roupas.getOrDefault(roupa, 0) + quantidade );
}

public String descricaoPedido() {
String descricao = "";

for ( Map.Entry<Roupa, Integer> entry : this.roupas.entrySet() ) {
descricao += "- " + entry.getKey().getDescricao() + ", Quantidade: " + entry.getValue() + "\n";
}

DecimalFormat d = new DecimalFormat("0.00");
descricao += "Valor: R$" + d.format( this.getValorTotal() ) + "\n";
descricao += "Forma de Pagamento: " + this.formaPagamento.getFormaPagamento();

return descricao;
}

}

class Pedidos implements Serializable{
private Map<Data, List<ItemPedido>> pedidos;

public Pedidos() {
this.pedidos = new HashMap<>();
}

public Map<Data, List<ItemPedido>> getPedidos() {
return this.pedidos;
}

public void removerPedido( Data data, ItemPedido pedido ) {
if (this.pedidos.containsKey(data)) {
this.pedidos.get(data).remove(pedido);

if (this.pedidos.get(data).isEmpty()) {

this.pedidos.remove(data);
}
}

}

public void adicionarPedido( Data data, ItemPedido pedido ) {

if ( !this.pedidos.containsKey(data) ) {
this.pedidos.put( data, new ArrayList<>() );
}

this.pedidos.get( data ).add( pedido );
}

public double getValorTotal( Data data ) {
double total = 0;
if (this.pedidos.containsKey( data )) {
for ( ItemPedido pedido : this.pedidos.get(data) ) {
total += pedido.getValorTotal();
}
}

return total;
}

public int getTotalPedidos( Data data ) {
if (this.pedidos.containsKey(data)) {

return this.pedidos.get( data ).size();
}

return 0;
}

public String descricaoPedidos( Data data ) {
String descricao = "";

if ( this.pedidos.containsKey(data) ) {

for ( ItemPedido pedido : this.pedidos.get(data) ) {
descricao += pedido.descricaoPedido() + "\n\n";
}

}

return descricao;
}

}

class Sistema {
private static Pedidos pedidos;

public static void main( String[] args ) {

pedidos = Persistencia.carregarPedidos();

if( pedidos == null ) {

pedidos = new Pedidos();

}

JFrame frame = new JFrame("Sistema de Controle de Vendas");
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.setSize(500, 400);

JPanel panel = new JPanel();
frame.getContentPane().add(panel);

JButton addButton = new JButton("Adicionar Pedido");
panel.add(addButton);

JButton displayButton = new JButton("Exibir Pedidos");
panel.add(displayButton);

JTextField tipoRoupaField = new JTextField(10);
JTextField tipoField = new JTextField(10);
JTextField corField = new JTextField(10);
JTextField marcaField = new JTextField(10);
JTextField tamanhoField = new JTextField(10);
JTextField quantidadeDisponivelField = new JTextField(10);
JTextField precoField = new JTextField(10);
JTextField quantidadeField = new JTextField(10);
String[] temas = { "Branco", "Preto" };
JComboBox<String> temaField = new JComboBox<>(temas);
        
String[] formasPagamento = { "PIX", "Cartão", "Dinheiro" };
JComboBox<String> formaPagamentoField = new JComboBox<>(formasPagamento);

JButton removeButton = new JButton("Remover Pedido");

panel.add(new JLabel("Tipo de Roupa:"));
panel.add(tipoRoupaField);
panel.add(new JLabel("Tipo:"));
panel.add(tipoField);
panel.add(new JLabel("Cor:"));
panel.add(corField);
panel.add(new JLabel("Marca:"));
panel.add(marcaField);
panel.add(new JLabel("Tamanho:"));
panel.add(tamanhoField);
panel.add(new JLabel("Quantidade Disponível:"));
panel.add(quantidadeDisponivelField);
panel.add(new JLabel("Preço:"));
panel.add(precoField);
panel.add(new JLabel("Tema:"));
panel.add(temaField);
panel.add(removeButton);


JPanel quantidadePanel = new JPanel();
quantidadePanel.add(new JLabel("Quantidade:"));
quantidadePanel.add(quantidadeField);
panel.add(quantidadePanel);
panel.add(new JLabel("Forma de Pagamento:"));
panel.add(formaPagamentoField);

addButton.addActionListener(e -> {
    try {

String tipoRoupa = tipoRoupaField.getText();
String tipo = tipoField.getText();
String cor = corField.getText();
String marca = marcaField.getText();
String tamanho = tamanhoField.getText();
int quantidadeDisponivel = Integer.parseInt(quantidadeDisponivelField.getText());
double preco = Double.parseDouble(precoField.getText());
int quantidade = Integer.parseInt(quantidadeField.getText());
String formaPagamento = (String) formaPagamentoField.getSelectedItem();
formaPagamento = Normalizer.normalize(formaPagamento, Normalizer.Form.NFD);
formaPagamento = formaPagamento.replaceAll("[^\\p{ASCII}]", "");
FormaPagamento formaPagamentoEnum = FormaPagamento.valueOf(formaPagamento.toUpperCase());

String dataS = JOptionPane.showInputDialog("Insira a data no formato dia/mês/ano:");
String[] partesData = dataS.split("/");
int dia = Integer.parseInt(partesData[0]);
int mes = Integer.parseInt(partesData[1]);
int ano = Integer.parseInt(partesData[2]);
Data data = new Data(dia, mes, ano);

adicionarPedido(tipoRoupa, tipo, cor, marca, tamanho, quantidadeDisponivel, preco, quantidade, data, formaPagamentoEnum);
Persistencia.salvarPedidos(pedidos);
JOptionPane.showMessageDialog(null, "Pedido adicionado");

} 

catch (MsgException ex) {
JOptionPane.showMessageDialog(null, ex.getMessage());
}

});

removeButton.addActionListener(e -> {
try {
String dataS = JOptionPane.showInputDialog("Insira a data do pedido a ser removido no formato dia/mês/ano:");
String[] parte = dataS.split("/");
int dia = Integer.parseInt(parte[0]);
int mes = Integer.parseInt(parte[1]);
int ano = Integer.parseInt(parte[2]);
Data data = new Data( dia, mes, ano );

if (dia == 0 && mes == 0 && ano == 0) {
List<ItemPedido> todosPedidos = new ArrayList<>();
for (List<ItemPedido> pedidosData : pedidos.getPedidos().values()) {
todosPedidos.addAll(pedidosData);
}
String[] opcoes = new String[todosPedidos.size()];
for (int i = 0; i < todosPedidos.size(); i++) {
opcoes[i] = "Pedido " + (i + 1) + ": " + todosPedidos.get(i).descricaoPedido();
}
String pedidoSelecionado = (String) JOptionPane.showInputDialog(null, "Selecione o pedido a ser removido:", "Remover Pedido", JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
int indicePedido = Arrays.asList(opcoes).indexOf(pedidoSelecionado);
if (indicePedido != -1) {
ItemPedido pedido = todosPedidos.get(indicePedido);
for (Data dataKey : pedidos.getPedidos().keySet()) {
pedidos.removerPedido(dataKey, pedido);
}
Persistencia.salvarPedidos(pedidos);
JOptionPane.showMessageDialog(null, "Pedido removido");
}
return;
}

if ( !pedidos.getPedidos().containsKey(data) ) {
JOptionPane.showMessageDialog(null, "Não há pedidos nesta data.");
return;
}

List<ItemPedido> pedidosData = pedidos.getPedidos().get( data );
String[] opcoes = new String[pedidosData.size()];

for (int i = 0; i < pedidosData.size(); i++) {
opcoes[i] = "Pedido " + (i + 1) + ": " + pedidosData.get(i).descricaoPedido();
}

String pedidoSelecionado = (String) JOptionPane.showInputDialog(null, "Selecione o pedido a ser removido:", "Remover Pedido", JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
int indicePedido = Arrays.asList(opcoes).indexOf(pedidoSelecionado);

if (indicePedido != -1) {
ItemPedido pedido = pedidosData.get(indicePedido);
pedidos.removerPedido(data, pedido);
Persistencia.salvarPedidos(pedidos);

JOptionPane.showMessageDialog(null, "Pedido removido");
}

} 
catch (MsgException ex) {
JOptionPane.showMessageDialog(null, ex.getMessage());
}

});

temaField.addActionListener(e -> {
String temaSelecionado = (String) temaField.getSelectedItem();
    
if (temaSelecionado.equals("Branco")) {
panel.setBackground(Color.WHITE);
    
for (Component component : panel.getComponents()) {
    
if (component instanceof JLabel) {
((JLabel) component).setForeground(Color.BLACK);
}
    
}
    
} else if (temaSelecionado.equals("Preto")) {
panel.setBackground(Color.BLACK);
    
for (Component component : panel.getComponents()) {
if (component instanceof JLabel) {
((JLabel) component).setForeground(Color.WHITE);
    
}
}
}
});

displayButton.addActionListener(e -> {
String dataString = JOptionPane.showInputDialog("Insira a data no formato dia/mês/ano:");
String[] partes = dataString.split("/");
int dia = Integer.parseInt(partes[0]);
int mes = Integer.parseInt(partes[1]);
int ano = Integer.parseInt(partes[2]);
Data data = new Data( dia, mes, ano );
    

String descricaoPedidos = "";
double valorTotal = 0;
int totalPedidos = 0;
for  (Data dataKey : pedidos.getPedidos().keySet() ) {

if (dia == 0 && dataKey.getMes() == mes && dataKey.getAno() == ano) {

for (ItemPedido pedido : pedidos.getPedidos().get(dataKey)) {
valorTotal += pedido.getValorTotal();
totalPedidos += pedido.getQuantidade();

 }

descricaoPedidos = "Valor total do mês " + mes + ": R$" + String.format("%.2f", valorTotal) + "\nQuantidade total de pedidos: " + totalPedidos;

} else if (dia == 0 && mes == 0 && dataKey.getAno() == ano) {

for (ItemPedido pedido : pedidos.getPedidos().get(dataKey)) {
valorTotal += pedido.getValorTotal();
totalPedidos += pedido.getQuantidade();
}

descricaoPedidos = "Valor total do ano " + ano + ": R$" + String.format("%.2f", valorTotal) + "\nQuantidade total de pedidos: " + totalPedidos;

} 

else if (dataKey.equals(data)) {

for (ItemPedido pedido : pedidos.getPedidos().get(dataKey)) {
valorTotal += pedido.getValorTotal();
totalPedidos += pedido.getQuantidade();
descricaoPedidos += pedido.descricaoPedido() + "\n";
}

descricaoPedidos = "Valor total do dia " + dia + "/" + mes + "/" + ano + ": R$" + String.format("%.2f", valorTotal) + "\nQuantidade total de pedidos: " + totalPedidos + "\n" + descricaoPedidos;

}
}
JOptionPane.showMessageDialog(null, descricaoPedidos);
});

frame.setVisible(true);
}

private static void adicionarPedido(String tipoRoupa, String tipo, String cor, String marca, String tamanho, int quantidadeDisponivel, double preco, int quantidade, Data data, FormaPagamento formaPagamento) {
Roupa roupa;

switch (tipoRoupa) {
case "Camisa":
roupa = new Camisa(tipo, cor, marca, tamanho, quantidadeDisponivel, preco);
break;

case "Short":
roupa = new Short(tipo, cor, marca, Integer.parseInt(tamanho), quantidadeDisponivel, preco);
break;

case "Intimo":
roupa = new Intimo(tipo, cor, marca, tamanho, quantidadeDisponivel, preco);
break;

default:
throw new MsgException("Tipo de roupa inválido");
}

ItemPedido itemPedido = new ItemPedido(formaPagamento);

try {
itemPedido.adicionarRoupa(roupa, quantidade);
} 
catch (MsgException e) {
JOptionPane.showMessageDialog(null, e.getMessage());
return;
}

pedidos.adicionarPedido(data, itemPedido);
}

}

class Persistencia {
public static void salvarPedidos(Pedidos pedidos) {

try {
FileOutputStream fileOut = new FileOutputStream("C:\\Users\\william franco\\Documents\\GitHub\\Trabalho-de-POO\\sistema\\pedidos.ser");
ObjectOutputStream out = new ObjectOutputStream(fileOut);
out.writeObject(pedidos);
out.close();
fileOut.close();
System.out.println("Dados salvos em 'pedidos.ser'");
} 

catch (IOException i) {
i.printStackTrace();
}
}

public static Pedidos carregarPedidos() {
Pedidos pedidos = null;
// SE OUTRA PESSOA USAR, MUDE O CAMINHO DO ARQUIVO PARA SALVAR NO SEU PROPRIO COMPUTADOR
Path path = Paths.get("C:\\Users\\william franco\\Documents\\GitHub\\Trabalho-de-POO\\sistema\\pedidos.ser");

if (Files.exists(path)) {
System.out.println("O arquivo 'pedidos.ser' existe.");

try {
// SE OUTRA PESSOA USAR, MUDE O CAMINHO DO ARQUIVO PARA SALVAR NO SEU PROPRIO COMPUTADOR
FileInputStream fileIn = new FileInputStream("C:\\Users\\william franco\\Documents\\GitHub\\Trabalho-de-POO\\sistema\\pedidos.ser");
ObjectInputStream in = new ObjectInputStream(fileIn);
pedidos = (Pedidos) in.readObject();
in.close();
fileIn.close();
} 

catch (IOException i) {
i.printStackTrace();
return null;
} 
            
catch (ClassNotFoundException c) {
System.out.println("Classe Pedidos não encontrada");
c.printStackTrace();
return null;

}

} else {
System.out.println("O arquivo 'pedidos.ser' não existe.");
}
return pedidos;
}
}